import unittest
from unittest.mock import create_autospec

from ..mq import factory as mq


class MessageMock:
    def __init__(self, k, v):
        self.k = k
        self.v = v

    def key(self):
        return self.k

    def value(self):
        return self.v

    def error(self):
        return False


class TestFactory(unittest.TestCase):

    def test_send(self):
        topic = "test"
        key = "42"
        value = "73"

        producers = []

        def create_producer(conf):
            from confluent_kafka import Producer
            mock = create_autospec(Producer)
            producers.append(mock)
            return mock

        provider = create_autospec(mq.Provider)
        provider.create_producer = create_producer
        connection_factory = mq.KafkaConnectionFactory(provider, "")
        factory = mq.Factory(connection_factory)
        connection = factory.create_connection()
        connection.send(topic, key, value)
        producers[0].produce.assert_called_with(topic, key=key, value=value)
        producers[0].poll.assert_called_with(1)

    def test_pool(self):
        topics = ["test"]

        key = "42"
        value = "73"

        consumers = []

        def create_consumer(conf):
            from confluent_kafka import Consumer
            mock = create_autospec(Consumer)
            mock.poll.return_value = MessageMock(key, value)
            consumers.append(mock)
            return mock

        provider = create_autospec(mq.Provider)
        provider.create_consumer = create_consumer
        connection_factory = mq.KafkaConnectionFactory(provider, "")
        factory = mq.Factory(connection_factory)
        connection = factory.create_connection()
        records = connection.pool(topics, "test", 1)
        self.assertTrue(len(records) > 0)
        record = records[0]
        self.assertTrue(record[0] == "42")
        self.assertTrue(record[1] == "73")


if __name__ == "__main__":
    unittest.main()
