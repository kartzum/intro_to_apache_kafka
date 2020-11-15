package aaa.abc.dd.k.plain.sources.simple;

import aaa.abc.dd.k.plain.sources.simple.imdb.MovieDirectScrapingExecutor;
import org.apache.commons.cli.*;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        run(args);
        // runMovieDirectScrapingExecutor(propertiesMovieDirectScrapingExecutor("localhost:9092", "kl", "q-data"));
    }

    static void run(String[] args) {
        Options opts = new Options();
        opts.addOption(Option.builder("o")
                .longOpt("option")
                .hasArg()
                .desc("Option")
                .build());
        opts.addOption(Option.builder("b")
                .longOpt("bootstrap-servers")
                .hasArg()
                .desc("Kafka cluster bootstrap server string (ex: broker:9092)")
                .build());
        opts.addOption(Option.builder("l")
                .longOpt("client-id")
                .hasArg()
                .desc("Kafka client-id")
                .build());
        opts.addOption(Option.builder("p")
                .longOpt("topic")
                .hasArg()
                .desc("Kafka topic")
                .build());
        CommandLine cl;
        try {
            cl = new DefaultParser().parse(opts, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (cl != null) {
            String option = cl.getOptionValue("option", "imdb-d-p-s");
            switch (option) {
                case "imdb-m-d-s-e":
                    String bootstrapServers = cl.getOptionValue("bootstrap-servers", "localhost:9092");
                    String clientId = cl.getOptionValue("client-id", "kl");
                    String topic = cl.getOptionValue("topic", "q-data");
                    Properties properties = propertiesMovieDirectScrapingExecutor(
                            bootstrapServers,
                            clientId,
                            topic
                    );
                    runMovieDirectScrapingExecutor(properties);
                    break;
            }
        }
    }

    static void runMovieDirectScrapingExecutor(Properties properties) {
        MovieDirectScrapingExecutor movieDirectScrapingExecutor =
                new MovieDirectScrapingExecutor(properties);
        movieDirectScrapingExecutor.run();
    }

    static Properties propertiesMovieDirectScrapingExecutor(
            String bootstrapServers,
            String clientId,
            String topic
    ) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap-servers", bootstrapServers);
        properties.setProperty("client-id", clientId);
        properties.setProperty("topic", topic);
        return properties;
    }
}
