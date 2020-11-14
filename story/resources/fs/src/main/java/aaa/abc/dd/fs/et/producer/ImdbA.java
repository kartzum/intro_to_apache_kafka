package aaa.abc.dd.fs.et.producer;

import aaa.abc.dd.fs.et.scraping.ImdbA.ImdbASimpleFactory;
import aaa.abc.dd.fs.et.scraping.ImdbA.ImdbASimple;
import aaa.abc.dd.fs.et.scraping.ImdbA.ImdbAItem;
import org.json.simple.JSONObject;

import java.util.*;

public class ImdbA {
    public static class ImdbASimpleProducer {
        private final String bootstrapServers;
        private final String clientId;
        private final String topic;

        private final String language;
        private final String startDate;
        private final String endDate;
        private final String countries;

        private final ImdbASimpleFactory factory;

        public ImdbASimpleProducer(
                String bootstrapServers,
                String clientId,
                String topic,
                String language,
                String startDate,
                String endDate,
                String countries,
                ImdbASimpleFactory factory
        ) {
            this.bootstrapServers = bootstrapServers;
            this.clientId = clientId;
            this.topic = topic;
            this.language = language;
            this.startDate = startDate;
            this.endDate = endDate;
            this.countries = countries;
            this.factory = factory;
        }

        public void execute() {
            ImdbASimple simple = factory.create(language, startDate, endDate, countries);
            try (SimpleStringStringProducer producer = new SimpleStringStringProducer(bootstrapServers, clientId, topic)) {
                Collection<ImdbAItem> items = simple.scrap();
                List<SimpleStringStringProducer.KeyValueStringString> kvList = new ArrayList<>();
                for (ImdbAItem item : items) {
                    Map<String, String> map = new HashMap<>();
                    map.put("url", item.url);
                    map.put("title", item.title);
                    map.put("short_text", item.shortText);
                    map.put("rating", Double.toString(item.rating));
                    map.put("genres", item.genres);
                    String value = JSONObject.toJSONString(map);
                    String key = UUID.randomUUID().toString();
                    kvList.add(new SimpleStringStringProducer.KeyValueStringString(key, value));
                }
                producer.produce(kvList);
            }
        }
    }

}
