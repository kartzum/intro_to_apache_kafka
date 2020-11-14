package aaa.abc.dd.fs.et.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Collection;
import java.util.Properties;

public class SimpleStringStringProducer implements AutoCloseable {
    private final String bootstrapServers;
    private final String clientId;
    private final String topic;

    private Producer<String, String> kafkaProducer;

    public SimpleStringStringProducer(String bootstrapServers, String clientId, String topic) {
        this.bootstrapServers = bootstrapServers;
        this.clientId = clientId;
        this.topic = topic;
    }

    public void produce(Collection<KeyValueStringString> keyValues) {
        if (kafkaProducer == null) {
            kafkaProducer = new KafkaProducer<>(createProperties());
        }
        for (KeyValueStringString kv : keyValues) {
            kafkaProducer.send(new ProducerRecord<>(topic, kv.key, kv.value));
        }
    }

    Properties createProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return properties;
    }

    @Override
    public void close() {
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }
    }

    public static class KeyValueStringString {
        public final String key;
        public final String value;

        public KeyValueStringString(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
