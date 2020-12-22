import sys
import logging

from pyspark.sql import SparkSession


def logging_init():
    logging.basicConfig(level=logging.INFO)


def logging_info(msg):
    logging.getLogger().info(msg)


def logging_exception(msg):
    logging.getLogger().exception(msg)


def main(args):
    result = 0
    spark = None
    try:
        logging_init()
        spark = SparkSession.builder.enableHiveSupport().getOrCreate()
        rdd = spark.sparkContext.parallelize([1, 2, 3, 4, 5])
        rdd_collected = rdd.collect()
        logging_info(rdd_collected)
    except Exception as e:
        logging_exception(e)
        result = 1
    finally:
        if spark is not None:
            spark.stop()
    return result


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
