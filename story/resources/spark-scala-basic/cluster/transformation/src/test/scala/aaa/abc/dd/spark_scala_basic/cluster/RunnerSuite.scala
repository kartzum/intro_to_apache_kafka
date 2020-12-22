package aaa.abc.dd.spark_scala_basic.cluster

import org.apache.spark.sql.SparkSession
import org.scalatest.{Matchers, Outcome, fixture}

class RunnerSuite extends fixture.FunSuite with Matchers {
  test("run") { spark =>
    import spark.implicits._

    val data = Seq((1, 2, 0), (3, 4, 0), (7, 8, 1))
    val df = data.toDF("x1", "x2", "label")

    val result = Runner.run(df)

    assert(result == 26.0)
  }

  override protected def withFixture(test: OneArgTest): Outcome = {
    val spark = SparkSession.builder().master("local[2]").getOrCreate()

    try withFixture(test.toNoArgTest(spark))

    finally spark.stop()
  }

  override type FixtureParam = SparkSession
}
