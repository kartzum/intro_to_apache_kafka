package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.streaming.StreamingQuery

class StructuredStreamingService {
  var service: LinearRegression = _
  var model: LinearRegressionModel = _

  def train(ds: DataFrame): Unit = {
    service = new LinearRegression().setMaxIter(10).setRegParam(0.01)
    model = service.fit(ds)
  }

  def predict(ds: DataFrame): StreamingQuery = {

    val m = ds.sparkSession.sparkContext.broadcast(model)

    def transformFun(features: org.apache.spark.ml.linalg.Vector): Double = {
      m.value.predict(features)
    }

    val transform: org.apache.spark.ml.linalg.Vector => Double = transformFun

    val toUpperUdf = udf(transform)

    val predictionDs = ds.withColumn("prediction", toUpperUdf(ds("features")))

    predictionDs
      .writeStream
      .foreachBatch((r: DataFrame, i: Long) => {
        r.show()
        // scalastyle:off println
        println(s"$i")
        // scalastyle:on println
      })
      .start()
  }
}
