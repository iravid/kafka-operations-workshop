name := "Kafka Operations"

scalaVersion := "2.12.10"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.4.4",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.4.4"
)

run / fork := true
run / connectInput := true
