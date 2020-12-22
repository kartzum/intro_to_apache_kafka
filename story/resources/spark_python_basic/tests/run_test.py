import unittest

from pyspark.sql import SparkSession

from spark_python_basic import spark_python_basic


class RunTest(unittest.TestCase):
    def test_run(self):
        spark = SparkSession.builder.enableHiveSupport().getOrCreate()

        data = [(1, 2, 0), (3, 4, 0), (7, 8, 1)]
        df = spark.createDataFrame(data=data, schema=["x1", "x2", "label"])

        r = spark_python_basic.run(df)

        self.assertEqual(r, 26.0)


if __name__ == "__main__":
    unittest.main()
