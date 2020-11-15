package aaa.abc.dd.k.plain.sources.simple.imdb;

import aaa.abc.dd.k.plain.sources.simple.common.KafkaServerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class MovieProducerTest {
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
                brokerHost, brokerPort, zooKeeperHost, zooKeeperPort
        )
        ) {
            kafkaServerService.start();
            kafkaServerService.createTopic(topic);

            String movie1Url = "1";
            String movie1Title = "2";
            String movie1Description = "3";
            Double movie1Rating = 4.0;
            String movie11Genres = "5";
            MovieDirectScrapingService movieDirectScrapingServiceImpl = () -> Collections.singleton(
                    new Data.Movie(movie1Url, movie1Title, movie1Description, movie1Rating, movie11Genres)
            );
            MovieProducer movieProducer =
                    new MovieProducer(bootstrapServers, clientId, topic, movieDirectScrapingServiceImpl);
            movieProducer.run();

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
                assertEquals(jsonObject.get("url"), movie1Url);
            });

            Thread.sleep(5000);
        }
    }
}
