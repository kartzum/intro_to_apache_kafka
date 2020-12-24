package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.rdd.RDD
import org.scalatest.{Matchers, Outcome, fixture}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.InputDStream

import scala.collection.mutable

class AServiceSuite extends fixture.FunSuite with Matchers {
  test("run") { spark =>
    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))

    val service = new AService.AService(Vectors.zeros(2))

    val trainData = mutable.Queue[RDD[Row]]()
    val trainDStream: InputDStream[Row] = ssc.queueStream(trainData)

    service.train(trainDStream)

    val predictData = mutable.Queue[RDD[Row]]()
    val predictDStream: InputDStream[Row] = ssc.queueStream(predictData)

    service.predict(predictDStream)

    ssc.start()

    val t1 = spark.sparkContext.makeRDD(Seq(Row(6.0, 1.0, 1.0)))
    val t2 = spark.sparkContext.makeRDD(Seq(Row(8.0, 1.0, 2.0)))
    val t3 = spark.sparkContext.makeRDD(Seq(Row(9.0, 2.0, 2.0)))
    val t4 = spark.sparkContext.makeRDD(Seq(Row(11.0, 2.0, 3.0)))


    trainData += t1
    trainData += t2
    trainData += t3
    trainData += t4

    Thread.sleep(2000)

    val p1 = spark.sparkContext.makeRDD(Seq(Row(1.0, 3.0, 5.0)))
    val p2 = spark.sparkContext.makeRDD(Seq(Row(2.0, 4.0, 6.0)))

    Thread.sleep(2000)

    predictData += p1
    predictData += p2

    ssc.awaitTerminationOrTimeout(10000)

    // Results: 1.0: 16.120067049547973
    // Results: 2.0: 20.009631478894576
  }

  override protected def withFixture(test: OneArgTest): Outcome = {
    val spark = SparkSession.builder().master("local[2]").getOrCreate()

    try withFixture(test.toNoArgTest(spark))

    finally spark.stop()
  }

  override type FixtureParam = SparkSession
}
