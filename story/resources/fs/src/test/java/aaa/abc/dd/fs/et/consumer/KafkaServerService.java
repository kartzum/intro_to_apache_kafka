package aaa.abc.dd.fs.et.consumer;

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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.nio.file.Files;
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
}