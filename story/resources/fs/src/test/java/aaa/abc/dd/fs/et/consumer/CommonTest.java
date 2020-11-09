package aaa.abc.dd.fs.et.consumer;

import aaa.abc.dd.fs.et.common.Service;
import org.junit.jupiter.api.Test;

public class CommonTest {

    @Test
    void simple() throws InterruptedException {
        String brokerHost = "127.0.0.1";
        int brokerPort = 29092;
        String bootstrapServers = brokerHost + ":" + brokerPort;
        String topic = "q-data";
        String groupId = "simple";
        try (KafkaServerService kafkaServerService = new KafkaServerService(brokerHost, brokerPort)) {
            kafkaServerService.start();
            kafkaServerService.createTopic(topic);

            kafkaServerService.send(topic, "42", "73");
            Thread.sleep(5000);

            Service service = new Common.Simple(bootstrapServers, groupId, topic);
            service.start();
        }
    }
}
