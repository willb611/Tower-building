
name := "tower-building"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
 "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
 "ch.qos.logback" % "logback-classic" % "1.2.3",

 "org.scalatest" %% "scalatest" % "3.0.4" % "test",
 "org.scalamock" %% "scalamock" % "4.0.0" % "test"
)
