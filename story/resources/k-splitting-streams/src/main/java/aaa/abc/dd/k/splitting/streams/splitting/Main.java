package aaa.abc.dd.k.splitting.streams.splitting;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        run(args);
        // tests();
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
        String optionName = config.getProperty("option.name", "split-stream");
        if("split-stream".equals(optionName)) {
            SplitStream.run(config);
        }
    }

    static Properties prepareConfig(Properties config) {
        return config;
    }

    static Properties defaultProperties() {
        Properties properties = new Properties();
        properties.setProperty("application.id", "splitting-app");
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("input.topic.name", "i");
        properties.setProperty("output.drama.topic.name", "d");
        properties.setProperty("output.fantasy.topic.name", "f");
        properties.setProperty("output.other.topic.name", "o");
        properties.setProperty("option.name", "split-stream");
        return properties;
    }

    static void tests() {
        test1();
    }

    static void test1() {
        Properties config = new Properties();
        config.setProperty("application.id", "splitting-app");
        config.setProperty("bootstrap.servers", "localhost:9092");
        config.setProperty("input.topic.name", "i");
        config.setProperty("output.drama.topic.name", "d");
        config.setProperty("output.fantasy.topic.name", "f");
        config.setProperty("output.other.topic.name", "o");
        config.setProperty("option.name", "split-stream");
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
