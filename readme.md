## Configurable two level parallel test execution in ScalaTest and SBT

This project is an example of configurable two level parallel test execution in ScalaTest and SBT.

By default SBT allows to run test suites in parallel. This setting can be configured by setting `parallelExecution` and `concurrentRestrictions` in `build.sbt` fe.:

```scala
val testConcurrency = 4
parallelExecution in Test := true
concurrentRestrictions in Global := Seq(Tags.limitAll(testConcurrency))
```

This way suites will be executed in parallel, but tests inside suite will be executed sequentially. It may cause whole test builds to be very long if some of suites contains either large number of tests or tests that takes long time.

ScalaTest enables us to use `ParallelTestExecution` trait to run tests in suite parallel fe.: 

```scala
class ExampleTest extends FlatSpec with ParallelTestExecution{
  // tests definition
}
```

This way tests in suite will be also executed in parallel. Number of concurrent tests within suite can be configured with `-P` param passed to ScalaTest runner. Unfortunately in newest versions of SBT this param is no longer supported. Without that setting default thread limit (2 * number of CPU threads) is applied. This limit applies to all tests in build so even if we pair it with SBT suite concurrency we cannot exceed this limitation nor configure lower limit fe. for tests that are more burdening for the system under test.

This issue was resolved by @drieks in his project [PageObject](https://github.com/agido/pageobject). He created an extension to ParallelTestExecution which allows to set number of threads per test suite. Based on his code I've created simplified solution which is limited to just one trait and can be used as replacement of `ParallelTestEcexution`:

```scala
trait FixedThreadPoolParallelExecution extends SuiteMixin with ParallelTestExecution{ this: Suite =>

  val threadPoolSize: Int

  abstract override def run(testName: Option[String], args: Args): Status =
    super.run(testName, args.copy(
      distributor = Some(
        new ConcurrentDistributor(
          args,
          java.util.concurrent.Executors.newFixedThreadPool(threadPoolSize, Executors.defaultThreadFactory)
        )
      )
    ))
}
``` 

It allows to set fixed size thread pool for each test suite so we are able to configure max number of concurrent tests fe. based on their complexity fe:

```scala
class LowComplexityTests extends FlatSpec with FixedThreadPoolParallelExecution{
  override val threadPoolSize: Int = 8 // run up to 8 tests in parallel
 
  //tests definition
}

class HighComplexityTests extends FlatSpec with FixedThreadPoolParallelExecution{
  override val threadPoolSize: Int = 2 // run up to 2 tests in parallel
 
  //tests definition
}
```

What is important is that those thread pools will be set per each test suite so if we pair it with SBT suite concurrency then max number of tests executed in parallel will be equal to sum of thread pools sizes. Also when `ParallelTestExecution` is enabled ScalaTest creates separate instance of test suite for each test which may lead to huge number of duplicated objects coexisting in system, so it is highly recommended to use [Shared fixtures](http://www.scalatest.org/user_guide/sharing_fixtures) to avoid multiplication.