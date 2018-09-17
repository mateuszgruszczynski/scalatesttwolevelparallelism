package eu.softwareqa.twolevelparallelism

import org.scalatest.{FixedThreadPoolParallelExecution, FlatSpec}

/**
  * Just a dummy test class that generates lot of tests
  * @param suiteId allows to add string id to distinguish suites
  * @param threadPoolSize limits number of concurrent tests in suite
 */
abstract class ExampleTestBase(suiteId: String, val threadPoolSize: Int) extends FlatSpec with FixedThreadPoolParallelExecution{

  behavior of "Example Test"

  for(n <- 1 to 50){
    it should s"just print $suiteId $n in a 10 sec loop" in {
      for(i <- 1 to 10){
        println(s"Suite number $suiteId test number $n iteration $i")
        Thread.sleep(1000)
      }
    }
  }

}

/**
  * Multiple test suits with multiple tests
  * Suites should be run in parallel by SBT
  * Test should be run in parallel by ScalaTest
  * Max total number of concurrent tests should be (number of sbt threads) * (threadPoolSize)
  */
class ExampleTest001 extends ExampleTestBase("001", 4)
class ExampleTest002 extends ExampleTestBase("002", 4)
class ExampleTest003 extends ExampleTestBase("003", 8)
class ExampleTest004 extends ExampleTestBase("004", 8)
class ExampleTest005 extends ExampleTestBase("005", 12)
class ExampleTest006 extends ExampleTestBase("006", 12)
class ExampleTest007 extends ExampleTestBase("007", 16)
class ExampleTest008 extends ExampleTestBase("008", 16)