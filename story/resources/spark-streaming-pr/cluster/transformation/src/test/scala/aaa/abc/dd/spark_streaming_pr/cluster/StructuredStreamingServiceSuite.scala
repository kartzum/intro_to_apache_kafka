package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.streaming.MemoryStream
import org.scalatest.{Matchers, Outcome, fixture}

class StructuredStreamingServiceSuite extends fixture.FunSuite with Matchers {
  test("run") { spark =>
    // Prepare data.
    val trainDs = spark.createDataFrame(Seq(
      (6.0, Vectors.dense(1.0, 1.0)),
      (8.0, Vectors.dense(1.0, 2.0)),
      (9.0, Vectors.dense(2.0, 2.0)),
      (11.0, Vectors.dense(2.0, 3.0))
    )).toDF("label", "features")
    // Create model and train.
    val service = new StructuredStreamingService()
    service.train(trainDs)
    // Predict and results.
    implicit val sqlCtx = spark.sqlContext
    import spark.implicits._
    val source = MemoryStream[Record]
    source.addData(Record(1.0, Vectors.dense(3.0, 5.0)))
    source.addData(Record(2.0, Vectors.dense(4.0, 6.0)))
    val predictDs = source.toDF()
    service.predict(predictDs).awaitTermination(2000)
    // +-----+---------+------------------+
    // |label| features|        prediction|
    // +-----+---------+------------------+
    // |  1.0|[3.0,5.0]|15.966990887541273|
    // |  2.0|[4.0,6.0]|18.961384020443553|
    // +-----+---------+------------------+
  }

  override protected def withFixture(test: OneArgTest): Outcome = {
    val spark = SparkSession.builder().master("local[2]").getOrCreate()

    try withFixture(test.toNoArgTest(spark))

    finally spark.stop()
  }

  override type FixtureParam = SparkSession

  case class Record(label: Double, features: org.apache.spark.ml.linalg.Vector)

}
