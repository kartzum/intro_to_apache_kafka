package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.regression.LinearRegression
import org.scalatest.{Matchers, Outcome, fixture}

class LinearRegressionSuite extends fixture.FunSuite with Matchers {
  test("simple") { spark =>
    linearRegression(spark)
  }

  def linearRegression(spark: SparkSession): Unit = {
    // Prepare data.
    val training = spark.createDataFrame(Seq(
      (6.0, Vectors.dense(1.0, 1.0)),
      (8.0, Vectors.dense(1.0, 2.0)),
      (9.0, Vectors.dense(2.0, 2.0)),
      (11.0, Vectors.dense(2.0, 3.0))
    )).toDF("label", "features")
    // Create model.
    val service = new LinearRegression().setMaxIter(10).setRegParam(0.01)
    // Train.
    val model = service.fit(training)
    // Predict.
    val test = spark.createDataFrame(Seq(
      (1.0, Vectors.dense(3.0, 5.0)),
      (2.0, Vectors.dense(4.0, 6.0))
    )).toDF("label", "features")
    val values = model.transform(test)
    // Results.
    values.select("label", "prediction")
      .collect()
      .foreach { case Row(label: Double, prediction: Double) =>
        println(s"($label) -> prediction=$prediction")
      }
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
