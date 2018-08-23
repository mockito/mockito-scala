package org.mockito.integrations.scalatest

import org.mockito._
import org.scalatest.{Outcome, Suite, TestSuite}

private[mockito] trait MockitoSessionFixture extends TestSuite { this: Suite =>

  abstract override def withFixture(test: NoArgTest): Outcome = {
    val session = MockitoScalaSession(name = s"${test.name} - session")
    val result = super.withFixture(test)
    // if the test has thrown an exception, the session will check first if the exception could be related to a mis-use
    // of mockito, if not, it will throw nothing so the real test failure can be reported by the ScalaTest
    session.finishMocking(result.toOption)
    result
  }
}
