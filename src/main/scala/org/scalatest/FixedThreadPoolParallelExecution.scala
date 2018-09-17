package org.scalatest

import java.util.concurrent.Executors

import org.scalatest.tools.ConcurrentDistributor

/**
  * Trait that sets number of threads in parallel execution by setting a fixed size thread pool
 */
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