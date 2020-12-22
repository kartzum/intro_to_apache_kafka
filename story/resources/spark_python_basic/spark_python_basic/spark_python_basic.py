import sys
import logging

from pyspark.sql import SparkSession


def logging_init():
    logging.basicConfig(level=logging.INFO)


def logging_info(msg):
    logging.getLogger().info(msg)


def logging_exception(msg):
    logging.getLogger().exception(msg)


def convert(items):
    for value in items:
        for element in value:
            yield element


def run(df):
    return sum(convert(df.groupBy().sum().collect()))


def main(args):
    result = 0
    spark = None
    try:
        logging_init()
        spark = SparkSession.builder.enableHiveSupport().getOrCreate()
        data = [(1, 2, 0), (3, 4, 0), (7, 8, 1)]
        df = spark.createDataFrame(data=data, schema=["x1", "x2", "label"])
        r = run(df)
        logging_info(r)
    except Exception as e:
        logging_exception(e)
        result = 1
    finally:
        if spark is not None:
            spark.stop()
    return result


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
