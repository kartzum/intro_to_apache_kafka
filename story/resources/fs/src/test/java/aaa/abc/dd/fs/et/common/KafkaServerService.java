package aaa.abc.dd.fs.et.common;

import java.util.function.Consumer;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.MockTime;
import kafka.utils.TestUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import kafka.zk.EmbeddedZookeeper;
import org.I0Itec.zkclient.ZkClient;
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
    String zkHost = "127.0.0.1";
    String brokerHost;
    int brokerPort;

    KafkaServer kafkaServer;
    ZkClient zkClient;
    EmbeddedZookeeper zkServer;
    ZkUtils zkUtils;

    public KafkaServerService(String brokerHost, int brokerPort) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
    }

    public void start() {
        zkServer = new EmbeddedZookeeper();
        String zkConnect = zkHost + ":" + zkServer.port();
        zkClient = new ZkClient(zkConnect, 30000, 30000, ZKStringSerializer$.MODULE$);
        zkUtils = ZkUtils.apply(zkClient, false);
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
        zkClient.close();
        zkServer.shutdown();
    }

    public void createTopic(String topic) {
        AdminUtils.createTopic(
                zkUtils, topic, 1, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);
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

        properties.put("bootstrap.servers", brokerHost + ":" + brokerPort);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", groupId);

        properties.put("enable.auto.commit", "false");
        properties.put("auto.offset.reset", "earliest");

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
            callback.accept(records);
        }
        kafkaConsumer.close();
    }
}