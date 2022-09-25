name := "scala3shenanigans"

version := "0.1"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "org.typelevel" %% "cats-core" % "2.8.0",
  "org.typelevel" %% "cats-effect" % "3.3.14",
  "org.typelevel" %% "log4cats-core" % "2.3.1",
  "org.typelevel" %% "log4cats-slf4j" % "2.3.1",
  "net.logstash.logback" % "logstash-logback-encoder" % "7.2",
)

lazy val commonSettings = Seq(
  scalaVersion := "3.2.0",
  scalacOptions += "-noindent",
  scalacOptions += "-rewrite",
  scalacOptions += "-print-lines",
  javacOptions += " --illegal-access=warn",
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
