package aaa.abc.dd.k.plain.sources.simple.imdb;

import java.time.LocalDate;
import java.util.*;

public class MovieDirectScrapingExecutor {
    private final Properties properties;

    public MovieDirectScrapingExecutor(Properties properties) {
        this.properties = properties;
    }

    public void run() {
        int countriesCounter = 0;
        List<String> countriesSource = Arrays.asList("us");

        while (true) {
            try {
                LocalDate localDate = LocalDate.now();

                int year = localDate.getYear();
                int month = localDate.getMonthValue();
                int day = localDate.getDayOfMonth();

                String monthString = month < 9 ? "0" + month : Integer.toString(month);
                String dayString = day < 9 ? "0" + day : Integer.toString(day);

                String startDate = year + "-" + monthString + "-" + dayString;
                String endDate = startDate;

                String language = "en";
                String countries = countriesSource.get(countriesCounter);

                execute(language, startDate, endDate, countries);

                Thread.sleep(1000);

                countriesCounter += 1;
                if (countriesCounter >= countriesSource.size()) {
                    countriesCounter = 0;
                }

            } catch (InterruptedException e) {
            }
        }
    }

    void execute(
            String language,
            String startDate,
            String endDate,
            String countries
    ) {
        String bootstrapServers = properties.getProperty("bootstrap.servers");
        String clientId = properties.getProperty("client.id");
        String topic = properties.getProperty("output.topic.name");

        MovieDirectScrapingService movieDirectScrapingService =
                new MovieDirectScrapingServiceImpl(language, startDate, endDate, countries, null, false);
        MovieProducer movieProducer =
                new MovieProducer(bootstrapServers, clientId, topic, movieDirectScrapingService);

        movieProducer.run();
    }
}
