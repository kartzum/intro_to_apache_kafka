package aaa.abc.dd.fs.et.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

// https://docs.confluent.io/current/clients/java.html

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

    static class Simple {
        private final String bootstrapServers;
        private final String clientId;
        private final String topic;

        Simple(String bootstrapServers, String clientId, String topic) {
            this.bootstrapServers = bootstrapServers;
            this.clientId = clientId;
            this.topic = topic;
        }

        public void start() {
            Properties properties = new Properties();
            properties.put("bootstrap.servers", bootstrapServers);
            properties.put("client.id", clientId);
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            Producer<String, String> kafkaProducer = new KafkaProducer<>(properties);
            kafkaProducer.send(new ProducerRecord<>(topic, "42", "73"));
            kafkaProducer.close();
        }
    }

    static void simple(String[] args) {
        String bootstrapServers = "localhost:9092";
        String clientId = "simple";
        String topic = "q-data";
        if (args.length > 0) {
            bootstrapServers = args[1];
            clientId = args[2];
            topic = args[3];
        }
        new Simple(bootstrapServers, clientId, topic).start();
    }
}
