package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.sql.SparkSession
import org.scalatest.{Matchers, Outcome, fixture}

class StructuredStreamingServiceSuite extends fixture.FunSuite with Matchers {
  /*test("run") { spark =>

  }*/

  override protected def withFixture(test: OneArgTest): Outcome = {
    val spark = SparkSession.builder().master("local[2]").getOrCreate()

    try withFixture(test.toNoArgTest(spark))

    finally spark.stop()
  }

  override type FixtureParam = SparkSession
}
