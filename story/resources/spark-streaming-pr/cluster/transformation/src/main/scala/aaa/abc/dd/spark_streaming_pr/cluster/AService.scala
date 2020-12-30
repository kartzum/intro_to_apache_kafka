package aaa.abc.dd.spark_streaming_pr.cluster

import aaa.abc.dd.spark_streaming_pr.cluster.DStreams._

import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.mllib.regression.{LabeledPoint, StreamingLinearRegressionWithSGD}
import org.apache.spark.sql.Row

class AService(val initialWeights: org.apache.spark.mllib.linalg.Vector) extends Train with Predict {
  var model: StreamingLinearRegressionWithSGD = _

  def train(ds: DStream[Row]): Unit = {
    val dsLabeled: DStream[LabeledPoint] =
      ds.map(r => {
        val values = r.toSeq
        val label = values.head.asInstanceOf[Double]
        val features = values.map(i => i.asInstanceOf[Double]).tail.toArray
        LabeledPoint(label, Vectors.dense(features))
      })

    model = new StreamingLinearRegressionWithSGD()
      .setInitialWeights(initialWeights)
      .setStepSize(0.01)
      .setMiniBatchFraction(0.5)

    model.trainOn(dsLabeled)
  }

  def predict(ds: DStream[Row]): Unit = {
    val dsLabeled: DStream[(Double, linalg.Vector)] =
      ds.map(r => {
        val values = r.toSeq
        val label = values.head.asInstanceOf[Double]
        val features = values.map(i => i.asInstanceOf[Double]).tail.toArray
        (label, Vectors.dense(features))
      })

    val r: DStream[(Double, Double)] = model.predictOnValues(dsLabeled)

    r.foreachRDD(d => {
      d.collect().foreach(v => {
        // scalastyle:off println
        println("Results: " + v._1 + ": " + v._2)
        // scalastyle:on println
      })
    })

  }
}
