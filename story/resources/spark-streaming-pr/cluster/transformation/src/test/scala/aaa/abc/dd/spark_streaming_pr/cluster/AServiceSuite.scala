package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.rdd.RDD
import org.scalatest.{Matchers, Outcome, fixture}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.InputDStream

import scala.collection.mutable

class AServiceSuite extends fixture.FunSuite with Matchers {
  test("run") { spark =>
    val streamingContext = new StreamingContext(spark.sparkContext, Seconds(1))

    val service = new AService.AService()

    val trainData = mutable.Queue[RDD[Row]]()
    val trainDStream: InputDStream[Row] = streamingContext.queueStream(trainData)

    service.train(trainDStream)

    val predictData = mutable.Queue[RDD[Row]]()
    val predictDStream: InputDStream[Row] = streamingContext.queueStream(predictData)

    service.predict(predictDStream)

    streamingContext.start()

    val t1 = spark.sparkContext.makeRDD(Seq(Row(1.0, 0.1, 0.2, 0.3)))
    val t2 = spark.sparkContext.makeRDD(Seq(Row(2.0, 0.0, 0.0, 0.3)))
    val t3 = spark.sparkContext.makeRDD(Seq(Row(3.0, 0.0, 0.2, 0.3)))

    trainData += t1
    trainData += t2
    trainData += t3

    Thread.sleep(2000)

    val p1 = spark.sparkContext.makeRDD(Seq(Row(1.0, 0.1, 0.2, 0.3)))
    val p2 = spark.sparkContext.makeRDD(Seq(Row(2.0, 0.3, 0.1, 0.2)))

    Thread.sleep(2000)

    predictData += p1
    predictData += p2

    streamingContext.awaitTerminationOrTimeout(10000)
  }

  override protected def withFixture(test: OneArgTest): Outcome = {
    val spark = SparkSession.builder().master("local[2]").getOrCreate()

    try withFixture(test.toNoArgTest(spark))

    finally spark.stop()
  }

  override type FixtureParam = SparkSession
}
