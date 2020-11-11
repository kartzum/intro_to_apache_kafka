from fastapi import FastAPI
from typing import Optional

host = "localhost"
port = 9092


class Provider:
    def create_producer(self, conf):
        pass

    def create_consumer(self, conf):
        pass


class Connection:
    def send(self, topic, key, value):
        pass

    def pool(self, topics, group_id, to):
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

    def pool(self, topics, group_id, to):
        import time
        conf = {
            "bootstrap.servers": self.urls,
            "group.id": group_id,
            "auto.offset.reset": "smallest"
        }
        consumer = self.kafka_provider.create_consumer(conf)
        consumer.subscribe(topics)
        records = []
        start_time = time.time()
        try:
            while True:
                msg = consumer.poll(timeout=1.0)
                current_time = time.time()
                t = current_time - start_time
                if t > to:
                    break
                if msg is None:
                    continue
                if msg.error():
                    raise Exception(msg.error())
                else:
                    k = msg.key()
                    v = msg.value()
                    record = (k, v)
                    records.append(record)
        except:
            pass
        return records


class KafkaProvider(Provider):
    def create_producer(self, conf):
        from confluent_kafka import Producer
        return Producer(conf)

    def create_consumer(self, conf):
        from confluent_kafka import Consumer
        return Consumer(conf)


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


app = FastAPI()


def k_consume(group_id, topics, timeout):
    connection = Factory(KafkaConnectionFactory(KafkaProvider(), host + ":" + str(port))).create_connection()
    records = connection.pool([topics], group_id, timeout)
    return records


def mq_consume(group_id, topics, timeout):
    return k_consume(group_id, topics, timeout)


def k_produce(topic, key, value):
    connection = Factory(KafkaConnectionFactory(KafkaProvider(), host + ":" + str(port))).create_connection()
    connection.send(topic, key, value)
    return {}


def mq_produce(topic, key, value):
    return k_produce(topic, key, value)


@app.get("/mq/consume")
def consume(group_id: str, topics: str, timeout: Optional[int] = 10):
    return mq_consume(group_id, topics, timeout)


@app.get("/mq/produce")
def produce(topic: str, key: str, value: str):
    return mq_produce(topic, key, value)

