package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.mllib.regression.{LabeledPoint, StreamingLinearRegressionWithSGD}

object AService {

  trait Train {
    def train(ds: DStream[LabeledPoint]): Unit
  }

  trait Predict {
    def predict(ds: DStream[(Double, linalg.Vector)]): Unit
  }

  class AService extends Train with Predict {
    var model: StreamingLinearRegressionWithSGD = _

    def train(ds: DStream[LabeledPoint]): Unit = {
      model = new StreamingLinearRegressionWithSGD()
        .setInitialWeights(Vectors.zeros(3))
        .setNumIterations(2)
        .setStepSize(0.1)
        .setMiniBatchFraction(0.25)

      model.trainOn(ds)

    }

    def predict(ds: DStream[(Double, linalg.Vector)]): Unit = {
      val r: DStream[(Double, Double)] = model.predictOnValues(ds)

      r.foreachRDD(d => {
        d.foreach(v => {
          // scalastyle:off println
          println("Results: " + v._1 + ": " + v._2)
          // scalastyle:on println
        })
      })

    }
  }

}
