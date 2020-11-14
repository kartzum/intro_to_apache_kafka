package aaa.abc.dd.fs.et.producer;

import aaa.abc.dd.fs.et.common.KafkaServerService;
import aaa.abc.dd.fs.et.scraping.ImdbA.ImdbASimpleFactory;
import aaa.abc.dd.fs.et.scraping.ImdbA.ImdbAItem;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static junit.framework.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImdbATest {
    @Test
    void simple() throws InterruptedException {
        String brokerHost = "127.0.0.1";
        int brokerPort = 29092;
        String zooKeeperHost = "127.0.0.1";
        int zooKeeperPort = 22183;
        String bootstrapServers = brokerHost + ":" + brokerPort;
        String topic = "q-data";
        String clientId = "simple";
        String language = "";
        String startDate = "";
        String endDate = "";
        String countries = "";
        ImdbASimpleFactory factory = mock(ImdbASimpleFactory.class);
        try (KafkaServerService kafkaServerService = new KafkaServerService(
                brokerHost, brokerPort, zooKeeperHost, zooKeeperPort
        )
        ) {
            kafkaServerService.start();
            kafkaServerService.createTopic(topic);

            String item1Url = "1";
            String item1Title = "2";
            String item1ShortText = "3";
            Double item1Rating = 4.0;
            String item1Genres = "5";

            when(factory.create(anyString(), anyString(), anyString(), anyString())).thenReturn(() -> Collections.singleton(
                    new ImdbAItem(item1Url, item1Title, item1ShortText, item1Rating, item1Genres)
            ));

            ImdbA.ImdbASimpleProducer producer = new ImdbA.ImdbASimpleProducer(
                    bootstrapServers,
                    clientId,
                    topic,
                    language,
                    startDate,
                    endDate,
                    countries,
                    factory
            );
            producer.execute();

            kafkaServerService.poll(topic, "simple", 1, 5, (records) -> {
                assertTrue(records.count() > 0);
                ConsumerRecord<String, String> record = records.iterator().next();
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = null;
                try {
                    jsonObject = (JSONObject) jsonParser.parse(record.value());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                assertNotNull(jsonObject);
                assertEquals(jsonObject.get("url"), item1Url);
            });

            Thread.sleep(5000);
        }
    }
}
