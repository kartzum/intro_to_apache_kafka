#!/bin/sh

SCRIPT_DIR=$(dirname "$0")

K_M=${1}
K_B=${2}
K_T=${3}

if [ -z ${K_ENV} ];
then
  source $SCRIPT_DIR/env.sh
fi

if [ -z ${K_JARS} ];
then
  K_JARS="/data/kafka/spark-sql-kafka-0-10_2.11-2.4.4.jar,/data/kafka/kafka-clients-2.4.0.jar"
fi

spark-submit \
--master yarn \
--deploy-mode client \
--num-executors 1 \
--executor-memory 2G \
--driver-memory 2G \
--jars $K_JARS \
${SCRIPT_DIR}/sb.py \
$K_M \
$K_B \
$K_T