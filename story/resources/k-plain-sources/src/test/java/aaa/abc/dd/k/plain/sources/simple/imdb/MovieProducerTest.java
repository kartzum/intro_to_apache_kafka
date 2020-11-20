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

            String movie1TitleId = "1";
            String movie1TitleUrl = "2";
            String movie1Title = "3";
            String movie1Description = "4";
            Double movie1Rating = 5.0;
            String movie1Genres = "6";
            String movie1Runtime = "7";
            String movie1BaseUrl = "8";
            String movie1BaseNameUrl = "9";
            String movie1BaseTitleUrl = "10";
            String movie1Ids = "11";
            String movie1Names = "12";
            String movie1DirectorIds = "13";
            String movie1DirectorNames = "14";
            String movie1Ranks = "15";
            String movie1participantCountMovies = "16";
            MovieDirectScrapingService movieDirectScrapingServiceImpl = () -> Collections.singleton(
                    new Data.Movie(
                            movie1TitleId,
                            movie1TitleUrl,
                            movie1Title,
                            movie1Description,
                            movie1Rating,
                            movie1Genres,
                            movie1Runtime,
                            movie1BaseUrl,
                            movie1BaseNameUrl,
                            movie1BaseTitleUrl,
                            movie1Ids,
                            movie1Names,
                            movie1DirectorIds,
                            movie1DirectorNames,
                            movie1Ranks,
                            movie1participantCountMovies
                    )
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
                assertEquals(jsonObject.get("title_url"), movie1TitleUrl);
            });

            Thread.sleep(5000);
        }
    }
}
