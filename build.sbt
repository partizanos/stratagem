name := "stratagem"

version := "0.1"

scalaVersion := "2.10.2"

parallelExecution in Test := false

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M8" % "test"

testOptions in Test += Tests.Argument("-oD")

// libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.2" % "test"

org.scalastyle.sbt.ScalastylePlugin.Settings