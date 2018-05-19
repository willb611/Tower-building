
name := "tower-building"

version := "0.1"

scalaVersion := "2.12.6"

val akkaVersion = "2.5.6"

concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)

libraryDependencies ++= Seq(
 "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",

 "org.scalatest" %% "scalatest" % "3.0.4" % "test",
 "org.scalamock" %% "scalamock" % "4.0.0" % "test",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion
)
