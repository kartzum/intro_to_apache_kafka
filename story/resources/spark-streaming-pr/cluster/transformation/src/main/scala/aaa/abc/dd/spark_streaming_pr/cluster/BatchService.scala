package aaa.abc.dd.spark_streaming_pr.cluster

import aaa.abc.dd.spark_streaming_pr.cluster.Batch._
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.sql.{DataFrame, Row}

// https://spark.apache.org/docs/latest/ml-classification-regression.html#linear-regression
// https://spark.apache.org/docs/latest/ml-pipeline.html

class BatchService extends Train with Predict {
  var service: LinearRegression = _
  var model: LinearRegressionModel = _

  override def train(ds: DataFrame): Unit = {
    service = new LinearRegression().setMaxIter(10).setRegParam(0.01)
    model = service.fit(ds)
  }

  override def predict(ds: DataFrame): Unit = {
    val r = model.transform(ds)
    r.select("label", "prediction")
      .collect()
      .foreach { case Row(label: Double, prediction: Double) =>
        // scalastyle:off println
        println(s"($label) -> prediction=$prediction")
        // scalastyle:on println
      }
  }
}
