from confluent_kafka import Consumer, KafkaException
import sys

bootstrap_servers = "localhost:9092"
topics = "q-data"

conf = {
    "bootstrap.servers": bootstrap_servers,
    "group.id": "simple",
    "auto.offset.reset": "smallest"
}

consumer = Consumer(conf)


def print_assignment(c, partitions):
    print("Assignment:", partitions)


consumer.subscribe(topics, on_assign=print_assignment)

try:
    while True:
        msg = consumer.poll(timeout=1.0)
        if msg is None:
            continue
        if msg.error():
            raise KafkaException(msg.error())
        else:
            print(msg.value())

except KeyboardInterrupt:
    sys.stderr.write("%% Aborted by user\n")

finally:
    consumer.close()
