package aaa.abc.dd.k.plain.features.simple;

import aaa.abc.dd.k.plain.features.simple.Data.*;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.test.TestRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static aaa.abc.dd.k.plain.features.simple.Constants.*;
import static org.junit.Assert.*;

public class FeaturesStreamTest {
    @Test
    public void simple() {
        StreamsBuilder builder = new StreamsBuilder();
        Properties properties = prepareConfig();
        FeaturesStream featuresStream = new FeaturesStream(properties);
        featuresStream.buildStreams(builder);
        try (TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), featuresStream.buildStreamsProperties())) {
            StringSerializer stringSerializer = new StringSerializer();
            StringDeserializer stringDeserializer = new StringDeserializer();

            Map<String, TestInputTopic<String, String>> inputTopics = new HashMap<>();
            for (FeatureDescriptor featureDescriptor : featuresStream.featuresDescriptor.featureDescriptors) {
                TestInputTopic<String, String> inputTopic =
                        testDriver.createInputTopic(featureDescriptor.source, stringSerializer, stringSerializer);
                inputTopics.put(featureDescriptor.source, inputTopic);
            }
            TestOutputTopic<String, String> outputTopic =
                    testDriver.createOutputTopic(featuresStream.featuresDescriptor.sinkSource, stringDeserializer, stringDeserializer);

            String key = "1";
            for (Map.Entry<String, TestInputTopic<String, String>> e : inputTopics.entrySet()) {
                Map<String, Object> map = new HashMap<>();
                map.put(e.getKey(), "value");
                e.getValue().pipeInput(key, JSONObject.toJSONString(map));
            }

            List<TestRecord<String, String>> testRecords = outputTopic.readRecordsToList();
            assertFalse(testRecords.isEmpty());
        }
    }

    @Test
    public void checkJoiner() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("1f1", "1v1");
        String json1 = JSONObject.toJSONString(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("2f1", "2v1");
        String json2 = JSONObject.toJSONString(map2);
        FeaturesStream.ValueJoinerSimple joiner = new FeaturesStream.ValueJoinerSimple();
        String jsonStringResult = joiner.apply(json1, json2);
        Object jsonObjectResult = null;
        JSONParser jsonParser = new JSONParser();
        try {
            jsonObjectResult = jsonParser.parse(jsonStringResult);
        } catch (ParseException e) {
        }
        assertNotNull(jsonObjectResult);
    }

    Properties prepareConfig() {
        Properties properties = new Properties();
        properties.setProperty("application.id", APP_ID);
        properties.setProperty("bootstrap.servers", "localhost:9091");
        properties.setProperty("option.name", DEFAULT_OPTION_NAME);
        properties.setProperty(FEATURES_DESCRIPTOR_FEATURE_DESCRIPTORS_SOURCES, "a,b,c");
        properties.setProperty(FEATURES_DESCRIPTOR_SINK_SOURCE, "o");
        return properties;
    }
}
