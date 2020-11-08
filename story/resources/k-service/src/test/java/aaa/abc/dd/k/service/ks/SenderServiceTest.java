package aaa.abc.dd.k.service.ks;

import org.junit.jupiter.api.Test;

public class SenderServiceTest {
    @Test
    void consumeEmail() throws InterruptedException {
        String brokerHost = "127.0.0.1";
        int brokerPort = 29092;
        String bootstrapServers = brokerHost + ":" + brokerPort;
        String senderTopic = "sender_data";
        try (KafkaServerService kafkaServerService = new KafkaServerService(brokerHost, brokerPort)) {
            kafkaServerService.start();
            kafkaServerService.createTopic(senderTopic);

            SenderService senderService = new SenderService(bootstrapServers, senderTopic);
            senderService.start();

            kafkaServerService.send(senderTopic, "42", "message");

            Thread.sleep(6000);

            senderService.stop();
        }
    }
}
