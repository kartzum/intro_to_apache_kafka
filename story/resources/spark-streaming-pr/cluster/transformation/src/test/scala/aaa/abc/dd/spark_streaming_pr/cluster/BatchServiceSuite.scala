package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
import org.scalatest.{Matchers, Outcome, fixture}

class BatchServiceSuite extends fixture.FunSuite with Matchers {
  test("simple") { spark =>
    // Prepare data.
    val trainDs = spark.createDataFrame(Seq(
      (6.0, Vectors.dense(1.0, 1.0)),
      (8.0, Vectors.dense(1.0, 2.0)),
      (9.0, Vectors.dense(2.0, 2.0)),
      (11.0, Vectors.dense(2.0, 3.0))
    )).toDF("label", "features")
    // Create model and train.
    val service = new BatchService()
    service.train(trainDs)
    // Predict and results.
    val predictDs = spark.createDataFrame(Seq(
      (1.0, Vectors.dense(3.0, 5.0)),
      (2.0, Vectors.dense(4.0, 6.0))
    )).toDF("label", "features")
    service.predict(predictDs)
    // (1.0) -> prediction=15.966990887541273
    // (2.0) -> prediction=18.961384020443553
  }

  override protected def withFixture(test: OneArgTest): Outcome = {
    val spark = SparkSession.builder().master("local[2]").getOrCreate()

    try withFixture(test.toNoArgTest(spark))

    finally spark.stop()
  }

  override type FixtureParam = SparkSession
}
