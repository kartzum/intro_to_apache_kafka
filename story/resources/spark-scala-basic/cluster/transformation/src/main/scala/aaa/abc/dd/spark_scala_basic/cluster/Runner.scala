package aaa.abc.dd.spark_scala_basic.cluster

import org.apache.spark.sql.SparkSession

object Runner {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[2]").enableHiveSupport().getOrCreate()
    val rdd = spark.sparkContext.parallelize(Seq(1, 2, 3, 4, 5))
    val rddCollected = rdd.collect()
    rddCollected.foreach(println)
  }
}
