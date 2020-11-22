package aaa.abc.dd.k.plain.features.simple;

import aaa.abc.dd.k.plain.features.simple.Data.*;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static aaa.abc.dd.k.plain.features.simple.Constants.*;

public class FeaturesStream {
    final Properties envProps;

    final FeaturesDescriptor featuresDescriptor;

    public FeaturesStream(Properties envProps) {
        this.envProps = envProps;
        this.featuresDescriptor = createFromProperties(envProps);
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
        Serde<String> stringSerde = Serdes.String();

        List<KStream<String, String>> streams = new ArrayList<>();

        for (FeatureDescriptor featureDescriptor : featuresDescriptor.featureDescriptors) {
            KStream<String, String> stream =
                    builder.stream(featureDescriptor.source, Consumed.with(stringSerde, stringSerde))
                            .map(new KeyValueMapperSimple(featureDescriptor.key));
            streams.add(stream);
        }

        if (streams.size() > 0) {
            if (streams.size() == 1) {
                KStream<String, String> stream = streams.get(0);
                stream.to(featuresDescriptor.sinkSource);
            } else {
                KStream<String, String> pref = streams.get(0);
                for (int i = 1; i < streams.size(); i++) {
                    KStream<String, String> cur = streams.get(i);
                    pref = pref.leftJoin(cur,
                            new ValueJoinerSimple(),
                            JoinWindows.of(Duration.ofSeconds(1)),
                            StreamJoined.with(
                                    Serdes.String(),
                                    Serdes.String(),
                                    Serdes.String())
                    );
                }
                pref.to(featuresDescriptor.sinkSource);
            }
        }
    }

    static class ValueJoinerSimple implements ValueJoiner<String, String, String> {
        @Override
        public String apply(String value1, String value2) {
            if (value2 == null) {
                return value1;
            }
            JSONParser jsonParser = new JSONParser();
            Object object1 = null;
            Object object2 = null;
            String result = "";
            try {
                object1 = jsonParser.parse(value1);
                object2 = jsonParser.parse(value2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (object1 != null && object2 != null) {
                JSONObject jsonObject1 = (JSONObject) object1;
                JSONObject jsonObject2 = (JSONObject) object2;
                jsonObject1.putAll(jsonObject2);
                result = jsonObject1.toJSONString();
            }
            return result;
        }
    }

    static class KeyValueMapperSimple implements KeyValueMapper<String, String, KeyValue<String, String>> {
        private final String keyFiledNamed;

        KeyValueMapperSimple(String keyFiledNamed) {
            this.keyFiledNamed = keyFiledNamed;
        }

        @Override
        public KeyValue<String, String> apply(String key, String value) {
            JSONParser jsonParser = new JSONParser();
            Object object = null;
            try {
                object = jsonParser.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = (JSONObject) object;
            String newKey = key;
            if (jsonObject != null) {
                newKey = jsonObject.get(keyFiledNamed).toString();
            }
            return new KeyValue<>(newKey, value);
        }
    }

    public static FeaturesDescriptor createFromProperties(Properties properties) {
        String sources = properties.getProperty(FEATURES_DESCRIPTOR_FEATURE_DESCRIPTORS_SOURCES);
        String keys = properties.getProperty(FEATURES_DESCRIPTOR_FEATURE_DESCRIPTORS_KEYS);
        String singSource = properties.getProperty(FEATURES_DESCRIPTOR_SINK_SOURCE);
        String[] sourcesArray = sources.split(",");
        String[] keysArray = keys.split(",");
        List<FeatureDescriptor> featureDescriptors = new ArrayList<>();
        for (int i = 0; i < sourcesArray.length; i++) {
            FeatureDescriptor featureDescriptor =
                    new FeatureDescriptor(sourcesArray[i], keysArray[i]);
            featureDescriptors.add(featureDescriptor);
        }
        return new FeaturesDescriptor(featureDescriptors, singSource);
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
