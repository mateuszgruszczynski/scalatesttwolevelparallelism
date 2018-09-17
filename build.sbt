name := "ScalaTestTwoLevelParallelism"

version := "0.1"

scalaVersion := "2.12.6"

// Limit to 4 suites running in parallel
val testConcurrency = 4
parallelExecution in Test := true
concurrentRestrictions in Global := Seq(Tags.limitAll(testConcurrency))

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.4"
)