package aaa.abc.dd.k.splitting.streams.splitting;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Properties;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SplitStreamTest {
    @Test
    void simple() {
        StreamsBuilder builder = new StreamsBuilder();
        SplitStream splitStream = new SplitStream(prepareConfig());
        splitStream.buildStreams(builder);
        try (TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), splitStream.buildStreamsProperties())) {
            StringSerializer stringSerializer = new StringSerializer();
            StringDeserializer stringDeserializer = new StringDeserializer();

            TestInputTopic<String, String> inputTopic =
                    testDriver.createInputTopic("i", stringSerializer, stringSerializer);

            TestOutputTopic<String, String> outputDTopic =
                    testDriver.createOutputTopic("d", stringDeserializer, stringDeserializer);
            TestOutputTopic<String, String> outputFTopic =
                    testDriver.createOutputTopic("f", stringDeserializer, stringDeserializer);
            TestOutputTopic<String, String> outputOTopic =
                    testDriver.createOutputTopic("o", stringDeserializer, stringDeserializer);

            inputTopic.pipeInput("1", "drama", Instant.ofEpochMilli(1L));
            KeyValue<String, String> outputF = outputDTopic.readKeyValue();
            assertThat(outputF, equalTo(KeyValue.pair("1", "drama")));

            assertTrue(outputFTopic.isEmpty());
            assertTrue(outputDTopic.isEmpty());
            assertTrue(outputOTopic.isEmpty());
        }
    }

    Properties prepareConfig() {
        Properties properties = new Properties();
        properties.setProperty("application.id", "test");
        properties.setProperty("bootstrap.servers", "localhost:9091");
        properties.setProperty("input.topic.name", "i");
        properties.setProperty("output.drama.topic.name", "d");
        properties.setProperty("output.fantasy.topic.name", "f");
        properties.setProperty("output.other.topic.name", "o");
        return properties;
    }
}
