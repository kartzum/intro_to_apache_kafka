import unittest
import warnings

from pyspark.sql import SparkSession
from pyspark.sql.functions import col, udf
from pyspark.sql.types import FloatType
from pyspark.ml.linalg import Vectors, VectorUDT

from spark_streaming_pp import structure_streaming_service


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
        service = structure_streaming_service.StructuredStreamingService()
        service.train(train_ds)

        # Predict and results.

        def extract_features(x):
            values = x.split(",")
            features_ = []
            for i in values[1:]:
                features_.append(float(i))
            features = Vectors.dense(features_)
            return features

        extract_features_udf = udf(extract_features, VectorUDT())

        def extract_label(x):
            values = x.split(",")
            label = float(values[0])
            return label

        extract_label_udf = udf(extract_label, FloatType())

        predict_ds = spark.readStream.format("text").option("path", "data/structured_streaming").load() \
            .withColumn("features", extract_features_udf(col("value"))) \
            .withColumn("label", extract_label_udf(col("value")))

        service.predict(predict_ds).awaitTermination(3)

        # +-----+------------------+
        # |label|        prediction|
        # +-----+------------------+
        # |  1.0|15.966990887541273|
        # |  2.0|18.961384020443553|
        # +-----+------------------+

    def setUp(self):
        warnings.filterwarnings("ignore", category=ResourceWarning)
        warnings.filterwarnings("ignore", category=DeprecationWarning)


if __name__ == "__main__":
    unittest.main()
