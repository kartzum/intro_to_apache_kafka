#!/bin/bash

K_PATH="/data/kafka/kafka_2.12-2.3.0-1/"
K_PRODUCER=$K_PATH"bin/kafka-console-producer.sh"
K_PRODUCER_L=$K_PRODUCER" --broker-list localhost:9092"

K_TOPIC="q-data"
K_VALUE="42:73"

echo $K_VALUE | $K_PRODUCER_L --topic $K_TOPIC --property key.separator=":" --property "parse.key=true"