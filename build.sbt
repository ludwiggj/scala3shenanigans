name := "scala-3-shenanigans"

version := "0.1"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.4.5",
  "org.typelevel" %% "cats-core" % "2.8.0",
  "org.typelevel" %% "cats-effect" % "3.4.8",
  "org.typelevel" %% "log4cats-core" % "2.5.0",
  "org.typelevel" %% "log4cats-slf4j" % "2.5.0",
  "net.logstash.logback" % "logstash-logback-encoder" % "7.3",
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)

lazy val commonSettings = Seq(
  scalaVersion := "3.4.0",
  scalacOptions += "-noindent",
  scalacOptions += "-rewrite",
  scalacOptions += "-print-lines",
  javacOptions += " --illegal-access=warn",
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
