package aaa.abc.dd.fs.et.consumer;

import aaa.abc.dd.fs.et.common.Service;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Common {
    public static void main(String[] args) {
        String example = "simple";
        if (args.length > 0) {
            example = args[0];
        }
        if ("simple".equals(example)) {
            simple(args);
        }
    }

    static class Simple implements Service {
        private final String bootstrapServers;
        private final String groupId;
        private final String topic;

        Simple(String bootstrapServers, String groupId, String topic) {
            this.bootstrapServers = bootstrapServers;
            this.groupId = groupId;
            this.topic = topic;
        }

        @Override
        public void start() {
            Properties properties = new Properties();

            properties.put("bootstrap.servers", bootstrapServers);
            properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("group.id", groupId);

            properties.put("enable.auto.commit", "false");
            properties.put("auto.offset.reset", "earliest");

            KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

            kafkaConsumer.subscribe(Collections.singleton(topic));

            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));

            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("key = %s, value = %s, offset = %d\n", record.key(), record.value(), record.offset());
            }

            kafkaConsumer.close();

        }

        @Override
        public void stop() {

        }
    }

    static void simple(String[] args) {
        String bootstrapServers = "localhost:9092";
        String groupId = "simple";
        String topic = "q-data";
        if (args.length > 3) {
            bootstrapServers = args[1];
            groupId = args[2];
            topic = args[3];
        }
        new Simple(bootstrapServers, groupId, topic).start();
    }
}
