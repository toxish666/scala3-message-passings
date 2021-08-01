import sbt._

object Dependencies {

  val scalaPb = "com.thesamet.scalapb" %% "scalapb-runtime" % V.scalaPb
  val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect
  val fs2 = "co.fs2" %% "fs2-core" % V.fs2
  val fs2kafka =
    "com.github.fd4s" %% "fs2-kafka" % V.fs2kafka

  val prometheusClient = "io.prometheus" % "simpleclient" % V.prometheus
  val prometheusClientCommon = "io.prometheus" % "simpleclient_common" % V.prometheus
  val prometheusClientHotspot = "io.prometheus" % "simpleclient_hotspot" % V.prometheus

  val logstash = "net.logstash.logback" % "logstash-logback-encoder" % V.logstash

  object V {
    val catsEffect = "3.2.1"
    val scalaPb = "0.11.4"
    val scaffeine = "4.0.2"
    val fs2 = "3.0.6"
    val fs2kafka = "2.1.0"
    val http4s = "0.23.0"
    val pbson = "0.0.19"
    val prometheus = "0.11.0"
    val pureconfig = "0.14.1"
    val sttp = "3.1.9"
    val logstash = "6.6"
  }

}
