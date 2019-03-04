package org.mockito.scalatest
import org.mockito.{MockitoScalaSession, Strictness}
import org.scalatest._

private[mockito] trait MockitoSessionFixture extends TestSuite { this: Suite =>

  val strictness: Strictness = Strictness.StrictStubs

  abstract override def withFixture(test: NoArgTest): Outcome = {
    val session = MockitoScalaSession(name = s"${test.name} - session", strictness)
    val result  = super.withFixture(test)
    // if the test has thrown an exception, the session will check first if the exception could be related to a mis-use
    // of mockito, if not, it will throw nothing so the real test failure can be reported by the ScalaTest
    session.finishMocking(result.toOption)
    result
  }
}

private[mockito] trait MockitoSessionAsyncFixture extends AsyncTestSuite { this: Suite =>

  val strictness: Strictness = Strictness.StrictStubs

  abstract override def withFixture(test: NoArgAsyncTest): FutureOutcome = {
    val session = MockitoScalaSession(name = s"${test.name} - session", strictness)
    val result  = super.withFixture(test)
    // if the test has thrown an exception, the session will check first if the exception could be related to a mis-use
    // of mockito, if not, it will throw nothing so the real test failure can be reported by the ScalaTest
    result.onCompletedThen(_.toOption.foreach(o => session.finishMocking(o.toOption)))
  }
}
