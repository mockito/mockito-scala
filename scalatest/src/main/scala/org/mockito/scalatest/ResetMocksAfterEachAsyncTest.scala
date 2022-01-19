package org.mockito.scalatest

import org.scalatest.{ AsyncTestSuite, FutureOutcome }

/**
 * It automatically resets each mock after a each test is run, useful when we need to pass the mocks to some framework once at the beginning of the test suite
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends PlaySpec with MockitoSugar with ResetMocksAfterEachAsyncTest}}}
 */
trait ResetMocksAfterEachAsyncTest extends AsyncTestSuite with ResetMocksAfterEachTestBase {

  override def withFixture(test: NoArgAsyncTest): FutureOutcome =
    super.withFixture(test).onCompletedThen(_ => resetAll())

}
