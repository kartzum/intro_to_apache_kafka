#!/bin/bash

K_PATH="/data/kafka/kafka_2.12-2.3.0-1/"
K_CONSUMER=$K_PATH"bin/kafka-console-consumer.sh"
K_CONSUMER_L=$K_CONSUMER" --bootstrap-server localhost:9092"

K_TOPIC="q-data"

$K_CONSUMER_L --topic $K_TOPIC --property print.key=true --property key.separator=":"