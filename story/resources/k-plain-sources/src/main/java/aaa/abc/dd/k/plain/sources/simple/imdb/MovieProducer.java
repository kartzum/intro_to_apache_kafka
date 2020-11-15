package aaa.abc.dd.k.plain.sources.simple.imdb;

import aaa.abc.dd.k.plain.sources.simple.common.SimpleStringStringProducer;
import org.json.simple.JSONObject;

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
                map.put("url", move.url);
                map.put("title", move.title);
                map.put("description", move.description);
                map.put("rating", Double.toString(move.rating));
                map.put("genres", move.genres);
                String value = JSONObject.toJSONString(map);
                String key = UUID.randomUUID().toString();
                kvList.add(new SimpleStringStringProducer.KeyValueStringString(key, value));
            }
            producer.produce(kvList);
        }
    }
}
