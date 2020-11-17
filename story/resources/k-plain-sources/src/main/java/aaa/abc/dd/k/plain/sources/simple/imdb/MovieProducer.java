package aaa.abc.dd.k.plain.sources.simple.imdb;

import aaa.abc.dd.k.plain.sources.simple.common.SimpleStringStringProducer;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class MovieProducer {
    private final String bootstrapServers;
    private final String clientId;
    private final String topic;

    private final MovieDirectScrapingService movieDirectScrapingService;

    public MovieProducer(
            String bootstrapServers,
            String clientId,
            String topic,
            MovieDirectScrapingService movieDirectScrapingService
    ) {
        this.bootstrapServers = bootstrapServers;
        this.clientId = clientId;
        this.topic = topic;
        this.movieDirectScrapingService = movieDirectScrapingService;
    }

    public void run() {
        try (SimpleStringStringProducer producer = new SimpleStringStringProducer(
                bootstrapServers, clientId, topic)) {
            Collection<Data.Movie> movies = movieDirectScrapingService.scrap();
            List<SimpleStringStringProducer.KeyValueStringString> kvList = new ArrayList<>();
            for (Data.Movie move : movies) {
                Map<String, String> map = new HashMap<>();
                map.put("title_id", move.titleId);
                map.put("title_url", move.titleUrl);
                map.put("title", move.title);
                map.put("description", move.description);
                map.put("rating", Double.toString(move.rating));
                map.put("genres", move.genres);
                map.put("runtime", move.runtime);
                map.put("base_url", move.baseUrl);
                map.put("base_name_url", move.baseNameUrl);
                map.put("base_title_url", move.baseTitleUrl);
                map.put("participant_ids", move.participantIds);
                map.put("participant_names", move.participantNames);
                map.put("director_ids", move.directorIds);
                map.put("director_names", move.directorNames);
                map.put("source", "imdb");
                String value = JSONObject.toJSONString(map);
                String key = move.titleId; // UUID.randomUUID().toString();
                kvList.add(new SimpleStringStringProducer.KeyValueStringString(key, value));
                // toFile("/tmp/" + key + ".json", value);
            }
            producer.produce(kvList);
        }
    }

    static void toFile(String fileName, String data) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
