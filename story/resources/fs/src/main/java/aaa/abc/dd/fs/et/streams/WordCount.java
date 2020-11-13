package aaa.abc.dd.fs.et.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class WordCount {
    public static void main(String[] args) {
        run(args);
    }

    static void run(String[] args) {
        String bootstrapServers = "localhost:9092";
        String appId = "wordcount";
        String clientId = "wordcount";
        String inputTopic = "q-data";
        String outputTopic = "w-data";
        if (args.length > 0) {
            bootstrapServers = args[0];
            appId = args[1];
            clientId = args[2];
            inputTopic = args[3];
            outputTopic = args[4];
        }

        KafkaStreams streams = null;
        try {
            StreamsBuilder builder = new StreamsBuilder();

            WordCount wordCount = new WordCount(bootstrapServers, appId, clientId, inputTopic, outputTopic);
            wordCount.buildStreams(builder);

            streams = new KafkaStreams(builder.build(), wordCount.createStreamsConfiguration());
        } finally {
            if (streams != null) {
                streams.cleanUp();
                streams.start();
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    final String bootstrapServers;
    final String appId;
    final String clientId;
    final String inputTopic;
    final String outputTopic;

    WordCount(
            String bootstrapServers,
            String appId,
            String clientId,
            String inputTopic,
            String outputTopic
    ) {
        this.bootstrapServers = bootstrapServers;
        this.appId = appId;
        this.clientId = clientId;
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
    }

    Properties createStreamsConfiguration() {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, clientId);
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, System.getProperty("java.io.tmpdir"));
        return streamsConfiguration;
    }

    void buildStreams(StreamsBuilder builder) {
        KStream<String, String> textLines = builder.stream(inputTopic);
        Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
                .groupBy((keyIgnored, word) -> word)
                .count();
        wordCounts.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
    }
}
