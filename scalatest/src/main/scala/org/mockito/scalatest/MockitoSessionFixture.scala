package org.mockito.scalatest

import org.mockito.{ MockitoScalaSession, Strictness }
import org.scalatest._

private[mockito] trait MockitoSessionFixture extends TestSuite { this: Suite =>

  val strictness: Strictness = Strictness.StrictStubs

  abstract override def withFixture(test: NoArgTest): Outcome = {
    val session = MockitoScalaSession(name = s"${test.name} - session", strictness)

    val result =
      try {
        super.withFixture(test)
      } catch {
        case t: Throwable =>
          session.finishMocking(Some(t))
          throw t
      }

    session.finishMocking(result.toOption)
    result
  }
}

private[mockito] trait MockitoSessionAsyncFixture extends AsyncTestSuite { this: Suite =>

  val strictness: Strictness = Strictness.StrictStubs

  abstract override def withFixture(test: NoArgAsyncTest): FutureOutcome = {
    val session = MockitoScalaSession(name = s"${test.name} - session", strictness)

    val result =
      try {
        super.withFixture(test)
      } catch {
        case t: Throwable =>
          session.finishMocking(Some(t))
          throw t
      }

    result.onOutcomeThen(o => session.finishMocking(o.toOption))
  }
}
