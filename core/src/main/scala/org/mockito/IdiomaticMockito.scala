package org.mockito

import org.mockito.stubbing.{Answer, DefaultAnswer, ScalaOngoingStubbing}
import org.mockito.MockitoSugar._
import org.mockito.VerifyMacro.{AtLeast, AtMost, OnlyOn, Times}
import org.mockito.WhenMacro._

import scala.language.experimental.macros
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

trait IdiomaticMockito extends MockCreator {

  override def mock[T <: AnyRef: ClassTag: TypeTag](name: String)(implicit defaultAnswer: DefaultAnswer): T =
    MockitoSugar.mock[T](name)

  override def mock[T <: AnyRef: ClassTag: TypeTag](mockSettings: MockSettings): T = MockitoSugar.mock[T](mockSettings)

  override def mock[T <: AnyRef: ClassTag: TypeTag](defaultAnswer: DefaultAnswer): T =
    MockitoSugar.mock[T](defaultAnswer)

  override def mock[T <: AnyRef: ClassTag: TypeTag](implicit defaultAnswer: DefaultAnswer): T =
    MockitoSugar.mock[T]

  override def spy[T](realObj: T): T = MockitoSugar.spy(realObj)

  override def spyLambda[T <: AnyRef: ClassTag](realObj: T): T = MockitoSugar.spyLambda(realObj)

  implicit class StubbingOps[T](stubbing: => T) {

    def shouldReturn: ReturnActions[T] = macro WhenMacro.shouldReturn[T]

    def shouldCallRealMethod: ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]

    def shouldThrow: ThrowActions[T] = macro WhenMacro.shouldThrow[T]

    def shouldAnswer: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]

    def wasCalled()(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacro[T]

    def wasNotCalled()(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasNotMacro[T]

    def wasCalled(t: Times)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacroTimes[T]

    def wasCalled(t: AtLeast)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacroAtLeast[T]

    def wasCalled(t: AtMost)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacroAtMost[T]

    def wasCalled(t: OnlyOn)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacroOnlyOn[T]
  }

  class Returned
  class ReturnedBy[R](v: R) {
    def by[M](mock: M): M = doReturn(v).when(mock)
  }

  class Answered
  class AnsweredBy(answer: Answer[Any]) {
    def by[M](mock: M): M = Mockito.doAnswer(answer).when(mock)
  }

  class RealMethod {
    def willBe(called: Called): Called = called
  }
  class Called {
    def by[M](mock: M): M = doCallRealMethod.when(mock)
  }

  class Thrown
  class ThrownBy(v: Throwable) {
    def by[M](mock: M): M = doThrow(v).when(mock)
  }

  val called        = new Called
  val thrown        = new Thrown
  val returned      = new Returned
  val answered      = new Answered
  val theRealMethod = new RealMethod

  implicit class DoSomethingOps[R](v: R) {
    def willBe(r: Returned): ReturnedBy[R] = new ReturnedBy(v)
    def willBe(a: Answered): AnsweredBy = v match {
      case f: Function0[_]                                => new AnsweredBy(invocationToAnswer(_ => f()))
      case f: Function1[_, _]                             => new AnsweredBy(functionToAnswer(f))
      case f: Function2[_, _, _]                          => new AnsweredBy(functionToAnswer(f))
      case f: Function3[_, _, _, _]                       => new AnsweredBy(functionToAnswer(f))
      case f: Function4[_, _, _, _, _]                    => new AnsweredBy(functionToAnswer(f))
      case f: Function5[_, _, _, _, _, _]                 => new AnsweredBy(functionToAnswer(f))
      case f: Function6[_, _, _, _, _, _, _]              => new AnsweredBy(functionToAnswer(f))
      case f: Function7[_, _, _, _, _, _, _, _]           => new AnsweredBy(functionToAnswer(f))
      case f: Function8[_, _, _, _, _, _, _, _, _]        => new AnsweredBy(functionToAnswer(f))
      case f: Function9[_, _, _, _, _, _, _, _, _, _]     => new AnsweredBy(functionToAnswer(f))
      case f: Function10[_, _, _, _, _, _, _, _, _, _, _] => new AnsweredBy(functionToAnswer(f))
      case other                                          => new AnsweredBy(invocationToAnswer(_ => other))
    }
  }

  implicit class ThrowSomethingOps[R <: Throwable](v: R) {
    def willBe(thrown: Thrown): ThrownBy = new ThrownBy(v)
  }

  class On
  class Never
  //noinspection UnitMethodIsParameterless
  case class NeverInstance[T <: AnyRef](mock: T) {
    def called: Unit               = verifyZeroInteractions(mock)
    def called(again: Again): Unit = verifyNoMoreInteractions(mock)
  }
  class Again

  val on                = new On
  val onlyHere          = new OnlyOn
  val never             = new Never
  val again             = new Again
  val once              = Times(1)
  val twice             = Times(2)
  val thrice            = Times(3)
  val threeTimes        = Times(3)
  val fourTimes         = Times(4)
  val fiveTimes         = Times(5)
  val sixTimes          = Times(6)
  val sevenTimes        = Times(7)
  val eightTimes        = Times(8)
  val nineTimes         = Times(9)
  val tenTimes          = Times(10)
  val atLeastOnce       = AtLeast(1)
  val atLeastTwice      = AtLeast(2)
  val atLeastThrice     = AtLeast(3)
  val atLeastThreeTimes = AtLeast(3)
  val atLeastFourTimes  = AtLeast(4)
  val atLeastFiveTimes  = AtLeast(5)
  val atLeastSixTimes   = AtLeast(6)
  val atLeastSevenTimes = AtLeast(7)
  val atLeastEightTimes = AtLeast(8)
  val atLeastNineTimes  = AtLeast(9)
  val atLeastTenTimes   = AtLeast(10)
  val atMostOnce        = AtMost(1)
  val atMostTwice       = AtMost(2)
  val atMostThrice      = AtMost(3)
  val atMostThreeTimes  = AtMost(3)
  val atMostFourTimes   = AtMost(4)
  val atMostFiveTimes   = AtMost(5)
  val atMostSixTimes    = AtMost(6)
  val atMostSevenTimes  = AtMost(7)
  val atMostEightTimes  = AtMost(8)
  val atMostNineTimes   = AtMost(9)
  val atMostTenTimes    = AtMost(10)

  implicit class VerificationOps[T <: AnyRef](mock: T) {
    def was(n: Never): NeverInstance[T] = NeverInstance(mock)
  }

  object InOrder {
    def apply(mocks: AnyRef*)(verifications: VerifyOrder => Unit): Unit = verifications(VerifyOrder.inOrder(mocks))
  }
}

/**
 * Simple object to allow the usage of the trait without mixing it in
 */
object IdiomaticMockito extends IdiomaticMockito
