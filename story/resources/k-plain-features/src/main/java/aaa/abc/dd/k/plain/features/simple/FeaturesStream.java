package aaa.abc.dd.k.plain.features.simple;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class FeaturesStream {
    private final Properties envProps;

    public FeaturesStream(Properties envProps) {
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

    }

    public static void run(Properties config) {
        StreamsBuilder builder = new StreamsBuilder();
        FeaturesStream featuresStream = new FeaturesStream(config);
        featuresStream.buildStreams(builder);
        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, featuresStream.buildStreamsProperties());
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
