package aaa.abc.dd.fs.et.streams;

import org.apache.kafka.streams.StreamsBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import org.apache.kafka.streams.TopologyTestDriver;

public class WordCountTest {
    private TopologyTestDriver testDriver;

    private String bootstrapServers = "localhost:19092";
    private String appId = "wordcount";
    private String clientId = "wordcount";
    private String inputTopicName = "i-data";
    private String outputTopicName = "o-data";

    @Before
    public void setup() {
        StreamsBuilder builder = new StreamsBuilder();

        WordCount wordCount = new WordCount(bootstrapServers, appId, clientId, inputTopicName, outputTopicName);

        wordCount.buildStreams(builder);

        testDriver = new TopologyTestDriver(builder.build(), wordCount.createStreamsConfiguration());
    }

    @After
    public void tearDown() {
        try {
            if (testDriver != null) {
                testDriver.close();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Test
    void simple() {

    }

}
