package org.mockito.specs2

import org.mockito.hamcrest.MockitoHamcrest
import org.mockito.internal.ValueClassExtractor
import org.mockito.matchers.DefaultMatcher
import org.mockito.{ArgumentMatchersSugar, IdiomaticMockitoBase}
import org.scalactic.Equality
import org.specs2.control.Exceptions.catchAll
import org.specs2.control.Throwablex
import org.specs2.matcher.{Expectable, MatchFailure, MatchResult, MatchSuccess, Matcher}

trait Specs2Mockito extends IdiomaticMockitoBase with ArgumentMatchersSugar with MockitoSpecs2Support {

  def checkCalls[Any] = new Matcher[Any] with Throwablex {
    def apply[S <: Any](s: Expectable[S]) =
      catchAll { s.value } { identity } match {
        case Right(v) =>
          MatchSuccess("The mock was called as expected", "The mock was not called as expected", createExpectable(v))
        case Left(e: AssertionError) =>
          MatchFailure(
            "The mock was called as expected",
            s"The mock was not called as expected: ${e.messageAndCause}",
            createExpectable(s.value, e.messageAndCause)
          )
        // unexpected error from inside Mockito itself
        case Left(e) =>
          throw e
      }
  }

  override type Verification = MatchResult[Any]
  override def verification(v: => Any): Verification = createExpectable(v).applyMatcher(checkCalls)

  implicit def defaultMatcher[T] = new DefaultMatcher[T] {
    override def registerDefaultMatcher(value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T]): T =
      value match {
        case m: org.hamcrest.Matcher[_]       => MockitoHamcrest.argThat[T](m.asInstanceOf[org.hamcrest.Matcher[T]])
        case m: org.specs2.matcher.Matcher[_] => argThat(m)
        case _                                => eqTo(value)
      }
  }
}
