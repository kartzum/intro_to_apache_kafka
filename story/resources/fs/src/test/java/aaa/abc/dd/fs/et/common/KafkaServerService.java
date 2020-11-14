package aaa.abc.dd.fs.et.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.MockTime;
import kafka.utils.TestUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaServerService implements AutoCloseable {
    String brokerHost;
    int brokerPort;
    String zooKeeperHost;
    int zooKeeperPort;

    KafkaServer kafkaServer;
    ZooKeeperServerService zooKeeperServerService;

    public KafkaServerService(
            String brokerHost,
            int brokerPort,
            String zooKeeperHost,
            int zooKeeperPort
    ) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
        this.zooKeeperHost = zooKeeperHost;
        this.zooKeeperPort = zooKeeperPort;
    }

    public void start() {
        zooKeeperServerService = new ZooKeeperServerService(zooKeeperHost, zooKeeperPort);
        zooKeeperServerService.start();
        String zkConnect = zooKeeperHost + ":" + zooKeeperPort;
        Properties brokerProps = new Properties();
        brokerProps.setProperty("zookeeper.connect", zkConnect);
        brokerProps.setProperty("broker.id", "0");
        try {
            brokerProps.setProperty("log.dirs", Files.createTempDirectory("kafka-").toAbsolutePath().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        brokerProps.setProperty("listeners", "PLAINTEXT://" + brokerHost + ":" + brokerPort);
        brokerProps.setProperty("offsets.topic.replication.factor", "1");
        KafkaConfig config = new KafkaConfig(brokerProps);
        MockTime mock = new MockTime();
        kafkaServer = TestUtils.createServer(config, mock);
    }

    @Override
    public void close() {
        kafkaServer.shutdown();
        zooKeeperServerService.close();
    }

    public void createTopic(String topic) {
        createTopic(topic, 1, (short) 1, new HashMap<>());
    }

    public void createTopic(
            String topic,
            int partitions,
            short replication,
            Map<String, String> topicConfig
    ) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerHost + ":" + brokerPort);
        try (final AdminClient adminClient = AdminClient.create(properties)) {
            final NewTopic newTopic = new NewTopic(topic, partitions, replication);
            newTopic.configs(topicConfig);
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException fatal) {
            throw new RuntimeException(fatal);
        }
    }

    public KafkaProducer<String, String> createKafkaProducerStringString() {
        Properties producerProps = new Properties();
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerHost + ":" + brokerPort);
        producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(producerProps);
    }

    public void send(String topic, String key, String value) {
        try (KafkaProducer<String, String> senderKafkaProducer = createKafkaProducerStringString()) {
            ProducerRecord<String, String> data = new ProducerRecord<>(topic, key, value);
            senderKafkaProducer.send(data);
        }
    }

    public KafkaConsumer<String, String> createKafkaConsumerStringString(String groupId) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerHost + ":" + brokerPort);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaConsumer<>(properties);
    }

    public void poll(
            String topic,
            String groupId,
            int seconds,
            int times,
            Consumer<ConsumerRecords<String, String>> callback
    ) {
        KafkaConsumer<String, String> kafkaConsumer = createKafkaConsumerStringString(groupId);
        kafkaConsumer.subscribe(Collections.singleton(topic));
        for (int i = 0; i < times; i++) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(seconds));
            if(!records.isEmpty()) {
                callback.accept(records);
            }
        }
        kafkaConsumer.close();
    }
}