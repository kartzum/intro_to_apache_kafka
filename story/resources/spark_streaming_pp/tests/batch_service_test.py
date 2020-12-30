import unittest
import warnings

from pyspark.sql import SparkSession
from pyspark.ml.linalg import Vectors

from spark_streaming_pp import batch_service


class RunTest(unittest.TestCase):
    def test_run(self):
        spark = SparkSession.builder.enableHiveSupport().getOrCreate()
        spark.sparkContext.setLogLevel("ERROR")
        # Prepare data.
        train_ds = spark.createDataFrame([
            (6.0, Vectors.dense([1.0, 1.0])),
            (8.0, Vectors.dense([1.0, 2.0])),
            (9.0, Vectors.dense([2.0, 2.0])),
            (11.0, Vectors.dense([2.0, 3.0]))
        ],
            ["label", "features"]
        )
        # Create model and train.
        service = batch_service.BatchService()
        service.train(train_ds)
        # Predict and results.
        predict_ds = spark.createDataFrame([
            (1.0, Vectors.dense([3.0, 5.0])),
            (2.0, Vectors.dense([4.0, 6.0]))
        ],
            ["label", "features"]
        )
        service.predict(predict_ds)
        # 1.0, 15.966990887541273
        # 2.0, 18.961384020443553

    def setUp(self):
        warnings.filterwarnings("ignore", category=ResourceWarning)
        warnings.filterwarnings("ignore", category=DeprecationWarning)


if __name__ == "__main__":
    unittest.main()
