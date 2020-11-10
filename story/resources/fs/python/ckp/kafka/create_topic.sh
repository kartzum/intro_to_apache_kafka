#!/bin/bash

K_PATH="/data/kafka/kafka_2.12-2.3.0-1/"
K_TOPICS=$K_PATH"bin/kafka-topics.sh"
K_TOPICS_L=$K_TOPICS" --bootstrap-server localhost:9092"

K_TOPIC="q-data"

$K_TOPICS_L --create --topic $K_TOPIC --partitions 1 --replication-factor 1
