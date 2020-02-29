package org.mockito.specs2

import org.mockito.captor.{ ArgCaptor, Captor }
import org.mockito.hamcrest.MockitoHamcrest
import org.mockito.internal.ValueClassExtractor
import org.mockito.matchers.DefaultMatcher
import org.mockito.stubbing.ScalaOngoingStubbing
import org.mockito.{ ArgumentMatchersSugar, IdiomaticStubbing, PostfixVerifications, Specs2VerifyMacro, VerifyInOrder, VerifyOrder }
import org.scalactic.{ Equality, Prettifier }
import org.specs2.control.Exceptions.catchAll
import org.specs2.control.Throwablex._
import org.specs2.matcher.{ Expectable, MatchFailure, MatchResult, MatchSuccess, Matcher }

import scala.reflect.ClassTag

trait Mockito extends IdiomaticStubbing with PostfixVerifications with ArgumentMatchersSugar with MockitoSpecs2Support {
  def checkCalls[Any] = new Matcher[Any] {
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

  implicit def defaultMatcher[T: Equality: ValueClassExtractor](implicit prettifier: Prettifier): DefaultMatcher[T] =
    new DefaultMatcher[T] {
      override def registerDefaultMatcher(value: T): T =
        value match {
          case m: org.hamcrest.Matcher[_]       => MockitoHamcrest.argThat[T](m.asInstanceOf[org.hamcrest.Matcher[T]])
          case m: org.specs2.matcher.Matcher[_] => argThat(m)
          case _                                => eqTo(value)
        }
    }

  /** create an object supporting 'was' and 'were' methods */
  def there = new Calls

  /**
   * class supporting 'was' and 'were' methods to forward mockito calls to the CallsMatcher matcher
   */
  class Calls {
    def were[T](calls: => T)(implicit order: VerifyOrder): Verification = macro Specs2VerifyMacro.wasMacro[T, Verification]
    def was[T](calls: T)(implicit order: VerifyOrder): Verification = macro Specs2VerifyMacro.wasMacro[T, Verification]
  }

  /** no calls made to the mock */
  def noCallsTo[T <: AnyRef](mocks: T*): Unit = ()

  /** no call made to the mock */
  def no[T <: AnyRef](mock: T): T = mock

  /** one call only made to the mock */
  def one[T <: AnyRef](mock: T): T = mock

  /** two calls only made to the mock */
  def two[T <: AnyRef](mock: T): T = mock

  /** three calls only made to the mock */
  def three[T <: AnyRef](mock: T): T = mock

  /** exactly n calls only made to the mock */
  def exactly[T <: AnyRef](n: Int)(mock: T): T = mock

  /** at least n calls made to the mock */
  def atLeast[T <: AnyRef](i: Int)(mock: T): T = mock

  /** at least 1 call made to the mock */
  def atLeastOne[T <: AnyRef](mock: T): T = mock

  /** at least 2 calls made to the mock */
  def atLeastTwo[T <: AnyRef](mock: T): T = mock

  /** at least 3 calls made to the mock */
  def atLeastThree[T <: AnyRef](mock: T): T = mock

  /** at most n calls made to the mock */
  def atMost[T <: AnyRef](i: Int)(mock: T): T = mock

  /** at most 1 call made to the mock */
  def atMostOne[T <: AnyRef](mock: T): T = mock

  /** at most 2 calls made to the mock */
  def atMostTwo[T <: AnyRef](mock: T): T = mock

  /** at most 3 calls made to the mock */
  def atMostThree[T <: AnyRef](mock: T): T = mock

  /** no more calls made to the mock */
  def noMoreCallsTo[T <: AnyRef](mocks: T*): Unit = ()

  implicit class Specs2IntOps(i: Int) {
    def times[T](obj: T): T = obj
  }

  implicit class Specs2Stubbing[T](s: ScalaOngoingStubbing[T]) {
    def thenReturns(value: T, values: T*): ScalaOngoingStubbing[T] = s.andThen(value, values: _*)
  }

  implicit class MatchResultOps[T](m: MatchResult[T]) {
    def andThen[O](calls: => O)(implicit order: VerifyOrder): Verification = macro Specs2VerifyMacro.wasMacro[O, Verification]
  }

  def got[T](calls: => T)(implicit order: VerifyOrder): Verification = macro Specs2VerifyMacro.wasMacro[T, Verification]

  def capture[T: ClassTag]: Captor[T] = ArgCaptor[T]

  def inOrder(mocks: AnyRef*) =
    VerifyInOrder(mocks.toList.flatMap {
      case i: Array[_] => i.asInstanceOf[Array[AnyRef]]
      case m           => List(m)
    })
}
