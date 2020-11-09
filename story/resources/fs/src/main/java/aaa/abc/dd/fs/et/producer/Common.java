package aaa.abc.dd.fs.et.producer;

import aaa.abc.dd.fs.et.common.Service;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

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
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            Producer<String, String> kafkaProducer = new KafkaProducer<>(properties);
            kafkaProducer.send(new ProducerRecord<>(topic, "42", "73"));
            kafkaProducer.close();
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
