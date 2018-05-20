
name := "tower-building"

version := "0.1"

scalaVersion := "2.12.6"

val akkaVersion = "2.5.12"
val akkaHttpVersion = "10.0.13"
// https://doc.akka.io/docs/akka-http/10.0.13/compatibility-guidelines.html#specific-versions-inter-op-discussion

concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)

libraryDependencies ++= Seq(
 "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
 "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
 "ch.qos.logback" % "logback-classic" % "1.2.3",

 "org.scalatest" %% "scalatest" % "3.0.4" % "test",
 "org.scalamock" %% "scalamock" % "4.0.0" % "test",

 "com.typesafe.akka" %% "akka-actor" % akkaVersion,
 "com.typesafe.akka" %% "akka-stream" % akkaVersion,
 "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
 "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",

 "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
 "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
 "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpVersion % "test"
)