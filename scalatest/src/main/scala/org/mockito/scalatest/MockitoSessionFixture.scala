package org.mockito.scalatest
import org.mockito.{MockitoScalaSession, Strictness}
import org.scalatest._

import scala.util.control.NonFatal

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

    // if the test has thrown an exception, the session will check first if the exception could be related to a mis-use
    // of mockito, if not, it will throw nothing so the real test failure can be reported by the ScalaTest
    val result = try {
      super.withFixture(test)
    } catch {
      case NonFatal(ex) =>
        session.finishMocking(Some(ex))
        throw ex
    }

    result.onOutcomeThen(o => session.finishMocking(o.toOption))
  }
}
