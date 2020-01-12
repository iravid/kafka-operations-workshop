package ops

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{functions => f}

object Aggregations {
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
      .option("subscribe", "word-count")
      .option("startingOffsets", "earliest")
      .load()
    
    val query = topic
      .select($"timestamp", f.explode(f.split($"value".cast("STRING"), " ")).as("word"))
      .withWatermark("timestamp", "1 minute")
      .groupBy(f.window($"timestamp", "5 minutes"), $"word")
      .agg(f.count($"word"))
      .writeStream
      .format("console")
      .start()

    scala.io.StdIn.readLine()

    query.stop()
    spark.stop()
  }
}
