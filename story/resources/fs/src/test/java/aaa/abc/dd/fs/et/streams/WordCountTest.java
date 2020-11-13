package aaa.abc.dd.fs.et.streams;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class WordCountTest {
    private String bootstrapServers = "localhost:19092";
    private String appId = "wordcount";
    private String clientId = "wordcount";
    private String inputTopicName = "i-data";
    private String outputTopicName = "o-data";

    private StringSerializer stringSerializer = new StringSerializer();
    private StringDeserializer stringDeserializer = new StringDeserializer();
    private LongDeserializer longDeserializer = new LongDeserializer();

    @Test
    void testOneWord() {
        StreamsBuilder builder = new StreamsBuilder();
        WordCount wordCount = new WordCount(bootstrapServers, appId, clientId, inputTopicName, outputTopicName);
        wordCount.buildStreams(builder);
        try (TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), wordCount.createStreamsConfiguration())) {
            TestInputTopic<String, String> inputTopic = testDriver.createInputTopic(inputTopicName, stringSerializer, stringSerializer);
            TestOutputTopic<String, Long> outputTopic = testDriver.createOutputTopic(outputTopicName, stringDeserializer, longDeserializer);

            inputTopic.pipeInput("Hello", Instant.ofEpochMilli(1L));
            KeyValue<String, Long> output = outputTopic.readKeyValue();
            assertThat(output, equalTo(KeyValue.pair("hello", 1L)));
            assertTrue(outputTopic.isEmpty());
        }
    }

    @Test
    public void shouldCountWords() {
        StreamsBuilder builder = new StreamsBuilder();
        WordCount wordCount = new WordCount(bootstrapServers, appId, clientId, inputTopicName, outputTopicName);
        wordCount.buildStreams(builder);
        try (TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), wordCount.createStreamsConfiguration())) {
            TestInputTopic<String, String> inputTopic = testDriver.createInputTopic(inputTopicName, stringSerializer, stringSerializer);
            TestOutputTopic<String, Long> outputTopic = testDriver.createOutputTopic(outputTopicName, stringDeserializer, longDeserializer);
            List<String> inputValues = Arrays.asList(
                    "w1", "w2", "w1"
            );
            Map<String, Long> expectedWordCounts = new HashMap<>();
            expectedWordCounts.put("w1", 2L);
            expectedWordCounts.put("w2", 1L);
            inputTopic.pipeValueList(inputValues, Instant.ofEpochMilli(1L), Duration.ofMillis(100L));
            assertThat(outputTopic.readKeyValuesToMap(), equalTo(expectedWordCounts));
        }
    }
}
