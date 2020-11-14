package aaa.abc.dd.fs.et.producer;

import aaa.abc.dd.fs.et.common.KafkaServerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

public class CommonTest {
    @Test
    void simple() throws InterruptedException {
        String brokerHost = "127.0.0.1";
        int brokerPort = 29092;
        String zooKeeperHost = "127.0.0.1";
        int zooKeeperPort = 22183;
        String bootstrapServers = brokerHost + ":" + brokerPort;
        String topic = "q-data";
        String clientId = "simple";
        try (KafkaServerService kafkaServerService = new KafkaServerService(
                brokerHost, brokerPort, zooKeeperHost, zooKeeperPort)
        ) {
            kafkaServerService.start();
            kafkaServerService.createTopic(topic);

            Common.Simple service = new Common.Simple(bootstrapServers, clientId, topic);
            service.start();

            kafkaServerService.poll(topic, "simple", 1, 5, (records) -> {
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value());
                }
            });

            Thread.sleep(5000);
        }
    }
}
