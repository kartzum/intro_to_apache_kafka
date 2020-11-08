package aaa.abc.dd.k.service.ks;

import org.junit.jupiter.api.Test;

import static aaa.abc.dd.k.service.ks.SenderFactory.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

            SenderService.EmailService emailService = mock(SenderService.EmailService.class);

            SenderService senderService = new SenderService(bootstrapServers, senderTopic, emailService);
            senderService.start();

            String recipients = "recipients";
            String title = "title";
            String message = "message";

            kafkaServerService.send(senderTopic, key(), createMessage(EMAIL_METHOD, recipients, title, message));

            Thread.sleep(6000);

            verify(emailService).send(recipients, title, message);

            senderService.stop();
        }
    }
}
