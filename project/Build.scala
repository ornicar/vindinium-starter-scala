import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{ Dist, outputDirectory, distJvmOptions }
import sbt._, Keys._

object VindiniumBot extends Build {

  lazy val bot = Project(
    id = "vindinium-bot",
    base = file("."),
    settings = buildSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-kernel" % "2.2.3",
        "com.typesafe.akka" %% "akka-slf4j" % "2.2.3",
        "ch.qos.logback" % "logback-classic" % "1.0.0",
        "com.typesafe.play" %% "play-json" % "2.2.1",
        "org.scalaj" %% "scalaj-http" % "0.3.12"
      ),
      distJvmOptions in Dist := "-Xms256M -Xmx256M",
      outputDirectory in Dist := file("target/bot")
    )
  )

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.jousse",
    version := "0.1",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-language:_", "-deprecation", "-unchecked"),
    crossPaths := false)
}
