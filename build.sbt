import Dependencies._

val scala3Version = "3.0.0"

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "proto"
)
Compile / PB.protoSources := Seq(
  baseDirectory.value / "protobuf-proto" / "kafka" / "events"
)

lazy val utils = (project in file("util"))

lazy val root = project.in(file("."))
  .aggregate(utils)
  .settings(
    name := "scala3-simple",
    version := "0.1.0",

    crossScalaVersions := Seq("2.13.5"),
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      pureConfig,

      mongoDriver,

      catsEffect,
      scalaPb,
      scalaPb % "protobuf",
      fs2,
      fs2kafka,

      prometheusClient,
      prometheusClientCommon,
      prometheusClientHotspot,
      logstash
    )
  )

test in assembly := {}
assemblyOutputPath in assembly := baseDirectory.value / "target" / "app.jar"
mainClass in assembly := Some("free.practice.Main")
assemblyMergeStrategy in assembly := {
  case x if x.contains("io.netty.versions.properties") => MergeStrategy.discard
  case x if x.contains("nowarn.class") => MergeStrategy.first
  case "module-info.class" => MergeStrategy.discard
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case PathList("org", "eclipse", "jetty", _ @ _*) => MergeStrategy.deduplicate
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}