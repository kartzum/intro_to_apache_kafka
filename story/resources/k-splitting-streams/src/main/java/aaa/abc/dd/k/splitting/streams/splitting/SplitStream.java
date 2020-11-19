package aaa.abc.dd.k.splitting.streams.splitting;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class SplitStream {
    private final Properties envProps;

    public SplitStream(Properties envProps) {
        this.envProps = envProps;
    }

    public Properties buildStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, envProps.getProperty("application.id"));
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, envProps.getProperty("bootstrap.servers"));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }

    public void buildStreams(StreamsBuilder builder) {
        final Serde<String> stringSerde = Serdes.String();
        String inputTopic = envProps.getProperty("input.topic.name");
        KStream<String, String> inputStream = builder.stream(inputTopic, Consumed.with(stringSerde, stringSerde));
        KStream<String, String>[] branches = inputStream
                .branch((key, value) -> value.contains("drama"),
                        (key, value) -> value.contains("fantasy"),
                        (key, value) -> true);
        branches[0].to(envProps.getProperty("output.drama.topic.name"));
        branches[1].to(envProps.getProperty("output.fantasy.topic.name"));
        branches[2].to(envProps.getProperty("output.other.topic.name"));
    }

    public static void run(Properties config) {
        StreamsBuilder builder = new StreamsBuilder();
        SplitStream splitStream = new SplitStream(config);
        splitStream.buildStreams(builder);
        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, splitStream.buildStreamsProperties());
        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
        }));
        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
