package aaa.abc.dd.spark_scala_basic.cluster

import org.apache.spark.sql.{DataFrame, SparkSession}

object Runner {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[2]").enableHiveSupport().getOrCreate()
    import spark.implicits._

    val data = Seq((1, 2, 0), (3, 4, 0), (7, 8, 1))
    val df = data.toDF("x1", "x2", "label")
    val result = run(df)
    // scalastyle:off println
    println(result)
    // scalastyle:on
  }

  def run(df: DataFrame): Double = {
    df.groupBy().sum().collect().flatMap(r => r.toSeq).map(i => i.toString.toDouble).sum
  }
}
