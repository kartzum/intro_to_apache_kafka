#!/bin/bash

K_PATH="/data/kafka/kafka_2.12-2.3.0-1/"
K_TOPICS=$K_PATH"bin/kafka-topics.sh"
K_TOPICS_L=$K_TOPICS" --zookeeper localhost:2181"

K_TOPIC="i"

$K_TOPICS_L --delete --topic $K_TOPIC
