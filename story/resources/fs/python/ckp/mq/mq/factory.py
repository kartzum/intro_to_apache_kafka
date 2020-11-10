class Provider:
    def create_producer(self, conf):
        pass


class Connection:
    def send(self, topic, key, value):
        pass

    def pool(self):
        pass


class ConnectionFactory:
    def create_connection(self):
        pass


class KafkaConnection(Connection):
    def __init__(self, kafka_provider, urls):
        self.kafka_provider = kafka_provider
        self.urls = urls

    def send(self, topic, key, value):
        conf = {
            "bootstrap.servers": self.urls
        }
        producer = self.kafka_provider.create_producer(conf)
        producer.produce(topic, key=key, value=value)
        producer.poll(1)

    def pool(self):
        pass


class KafkaProvider(Provider):
    def create_producer(self, conf):
        from confluent_kafka import Producer
        return Producer(conf)


class KafkaConnectionFactory(ConnectionFactory):
    def __init__(self, kafka_provider, urls):
        self.kafka_provider = kafka_provider
        self.urls = urls

    def create_connection(self):
        return KafkaConnection(self.kafka_provider, self.urls)


class Factory:
    def __init__(self, connection_factory):
        self.connection_factory = connection_factory

    def create_connection(self):
        return self.connection_factory.create_connection()


def kafka_simple_send():
    provider = KafkaProvider()
    connection_factory = KafkaConnectionFactory(provider, "localhost:9092")
    factory = Factory(connection_factory)

    connection = factory.create_connection()
    connection.send("q-data", "42", "73")


def main():
    kafka_simple_send()


if __name__ == "__main__":
    main()
