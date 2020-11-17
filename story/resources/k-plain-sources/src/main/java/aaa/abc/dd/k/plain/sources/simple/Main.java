package aaa.abc.dd.k.plain.sources.simple;

import aaa.abc.dd.k.plain.sources.simple.imdb.MovieDirectScrapingExecutor;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static aaa.abc.dd.k.plain.sources.simple.common.ConnectionUtils.applyProxy;

public class Main {
    public static void main(String[] args) {
        applyProxy();
        run(args);
    }

    static void run(String[] args) {
        Options opts = new Options();
        opts.addOption(Option.builder("c")
                .longOpt("config-file")
                .hasArg()
                .desc("Java properties file with configurations")
                .build());
        opts.addOption(Option.builder("h").longOpt("help").hasArg(false).desc("Show usage information").build());
        CommandLine cl;
        try {
            cl = new DefaultParser().parse(opts, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (cl.hasOption("h")) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Application help", opts);
        } else {
            Properties config = Optional.ofNullable(cl.getOptionValue("config-file", null))
                    .map(path -> {
                        try {
                            return buildPropertiesFromConfigFile(path);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElse(defaultProperties());

            run(prepareConfig(config));
        }
    }

    static void run(Properties config) {
        String optionName = config.getProperty("option.name", "imdb-m-d-s-e");
        if ("imdb-m-d-s-e".equals(optionName)) {
            MovieDirectScrapingExecutor movieDirectScrapingExecutor =
                    new MovieDirectScrapingExecutor(config);
            movieDirectScrapingExecutor.run();
        }
    }

    static Properties prepareConfig(Properties config) {
        return config;
    }

    static Properties defaultProperties() {
        Properties properties = new Properties();
        properties.setProperty("application.id", "simple-app");
        properties.setProperty("client.id", "simple-app");
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("output.topic.name", "q-data");
        properties.setProperty("option.name", "imdb-m-d-s-e");
        return properties;
    }

    static void tests() {
        test1();
    }

    static void test1() {
        Properties config = new Properties();
        config.setProperty("application.id", "simple-app");
        config.setProperty("client.id", "simple-app");
        config.setProperty("bootstrap.servers", "localhost:9092");
        config.setProperty("output.topic.name", "q-data");
        config.setProperty("option.name", "imdb-m-d-s-e");
        run(config);
    }

    static Properties buildPropertiesFromConfigFile(final String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            properties.load(inputStream);
        }
        return properties;
    }
}
