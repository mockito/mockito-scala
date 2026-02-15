package org.mockito.scalatest

import org.scalatest.{ Outcome, TestSuite }

/**
 * It automatically resets each mock after each test is run, useful when we need to pass the mocks to some framework once at the beginning of the test suite
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends PlaySpec with MockitoSugar with ResetMocksAfterEachTest}}}
 */
trait ResetMocksAfterEachTest extends TestSuite with ResetMocksAfterEachTestCompat {

  override protected def withFixture(test: NoArgTest): Outcome = {
    val outcome = super.withFixture(test)
    resetAll()
    outcome
  }

}
