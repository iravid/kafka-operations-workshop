package ops

import org.apache.spark.sql.SparkSession

object TopicCopier {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Kafka Ops")
      .master("local[*]")
      .config("spark.sql.streaming.checkpointLocation", "/tmp/checkpoints")
      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.setLogLevel("ERROR")

    val topic = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "test-topic")
      .option("startingOffsets", "earliest")
      .load()

    val query = topic
      .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "copied-topic")
      .queryName("topic-copier")
      .start()

    scala.io.StdIn.readLine()

    query.stop()
    spark.stop()
  }
}
