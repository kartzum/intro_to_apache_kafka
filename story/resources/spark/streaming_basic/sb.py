import sys
import json

from pyspark import SparkContext
from pyspark.sql import SparkSession
from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils, TopicAndPartition
from pyspark.sql.functions import col, json_tuple


def m1(brokers, topic):
    sc = SparkContext()
    ssc = StreamingContext(sc, 1)

    stream = KafkaUtils.createDirectStream(ssc, [topic], {"metadata.broker.list": brokers})

    def transform(v):
        j = json.loads(v)
        m = {
            "a": j["event_time"],
            "b": j["clnt"]
        }
        r = json.dumps(m)
        return r

    st = stream.map(lambda v: transform(v[1]))
    st.pprint()

    ssc.start()

    ssc.awaitTermination()


def m2(brokers, topic):
    spark = SparkSession.builder.enableHiveSupport().getOrCreate()

    df = spark.readStream.format("kafka") \
        .option("kafka.bootstrap.servers", brokers) \
        .option("subscribe", topic) \
        .option("startingOffsets", "earliest") \
        .load() \
        .withColumn("value", col("value").cast("string").alias("value")) \
        .select(col("value")) \
        .select(json_tuple(col("value"), "event_time", "clnt").alias("a", "b"))

    def transform(batch_df, batch_id):
        batch_df.show()

    df \
        .writeStream \
        .outputMode("update") \
        .format("console") \
        .foreachBatch(transform) \
        .trigger(processingTime="2 seconds") \
        .option("checkpointLocation", "/tmp/sb") \
        .option("truncate", "true") \
        .start() \
        .awaitTermination()


def main(args):
    m = args[1]
    brokers = args[2]
    topic = args[3]
    if m == "m1":
        m1(brokers, topic)
    elif m == "m2":
        m2(brokers, topic)


if __name__ == "__main__":
    sys.exit(main(sys.argv))
