package aaa.abc.dd.spark_streaming_pr.cluster

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.Row
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

object DStreams {
  def createKafkaInputDStream(streamingContext: StreamingContext,
                              bootstrapServers: String,
                              groupId: String,
                              topic: String,
                              label: String,
                              features: Array[String]
                             ): DStream[Row] = {
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> bootstrapServers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array(topic)
    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(r => transform(r.value(), label, features))
  }

  def transform(value: String, label: String, features: Array[String]): Row = {
    val jsonParser = new JSONParser()
    val jsonObject = jsonParser.parse(value).asInstanceOf[JSONObject]
    val labelValue = jsonObject.get(label).toString.toDouble
    val featuresValues = features.map(f => jsonObject.get(f).toString.toDouble)
    val allValues: Array[Double] = Array(labelValue) ++ featuresValues
    Row.fromSeq(allValues)
  }
}
