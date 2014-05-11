name := "jpeg_encode_decode"

version := "1.0.0"

scalaVersion := "2.10.4"

sbtVersion := "0.13.2"

scalacOptions ++= Seq("-deprecation")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.4" % "test",
  "junit" % "junit" % "4.11" % "test",
  "org.specs2" % "specs2_2.10" % "2.3.11",
  "com.typesafe.akka" % "akka-actor_2.10" % "2.3.2"
)

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"


