package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.sql.DataFrame

object Batch {

  trait Train {
    def train(ds: DataFrame): Unit
  }

  trait Predict {
    def predict(ds: DataFrame): Unit
  }

}
