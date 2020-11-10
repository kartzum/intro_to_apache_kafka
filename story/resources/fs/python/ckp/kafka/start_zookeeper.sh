#!/bin/bash

K_PATH="/data/kafka/kafka_2.12-2.3.0-1/"
K_ZOOKEEPER_START=$K_PATH"bin/zookeeper-server-start.sh"
K_ZOOKEEPER_CONFIG=$K_PATH"config/zookeeper.properties"

$K_ZOOKEEPER_START $K_ZOOKEEPER_CONFIG
