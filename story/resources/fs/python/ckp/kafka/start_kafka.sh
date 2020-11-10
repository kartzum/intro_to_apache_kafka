#!/bin/bash

K_PATH="/data/kafka/kafka_2.12-2.3.0-1/"
K_KAFKA_START=$K_PATH"bin/kafka-server-start.sh"
K_KAFKA_CONFIG=$K_PATH"config/server.properties"

$K_KAFKA_START $K_KAFKA_CONFIG
