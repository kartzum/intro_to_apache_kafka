import unittest
from unittest.mock import create_autospec

from ..mq import factory as mq


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


if __name__ == "__main__":
    unittest.main()
