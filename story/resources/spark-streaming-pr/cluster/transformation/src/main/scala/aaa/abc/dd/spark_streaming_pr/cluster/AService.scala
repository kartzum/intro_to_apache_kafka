package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.mllib.regression.{LabeledPoint, StreamingLinearRegressionWithSGD}
import org.apache.spark.sql.Row

object AService {

  trait Train {
    def train(ds: InputDStream[Row]): Unit
  }

  trait Predict {
    def predict(ds: InputDStream[Row]): Unit
  }

  class AService extends Train with Predict {
    var model: StreamingLinearRegressionWithSGD = _

    def train(ds: InputDStream[Row]): Unit = {
      val dsLabeled: DStream[LabeledPoint] =
        ds.map(r => {
          val values = r.toSeq
          val label = values.head.asInstanceOf[Double]
          val features = values.map(i => i.asInstanceOf[Double]).tail.toArray
          LabeledPoint(label, Vectors.dense(features))
        })

      model = new StreamingLinearRegressionWithSGD()
        .setInitialWeights(Vectors.zeros(3))
        .setNumIterations(2)
        .setStepSize(0.1)
        .setMiniBatchFraction(0.25)

      model.trainOn(dsLabeled)

    }

    def predict(ds: InputDStream[Row]): Unit = {
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

}
