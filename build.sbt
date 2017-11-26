
name := "tower-building"

version := "0.1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.6"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies ++= Seq(
 "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
 "ch.qos.logback" % "logback-classic" % "1.2.3",
 "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

 "org.scalatest" %% "scalatest" % "3.0.4" % "test",
 "org.scalamock" %% "scalamock" % "4.0.0" % "test",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion
)
