package org.mockito

import org.mockito.VerifyMacro._
import org.mockito.WhenMacro._
import org.mockito.stubbing.ScalaOngoingStubbing

object IdiomaticMockitoBase {
  object Returned
  object ReturnedBy {
    def by[T](stubbing: T): T = macro DoSomethingMacro.returnedBy[T]
  }

  object Answered
  object AnsweredBy {
    def by[T](stubbing: T): T = macro DoSomethingMacro.answeredBy[T]
  }

  object Thrown
  object ThrownBy {
    def by[T](stubbing: T): T = macro DoSomethingMacro.thrownBy[T]
  }

  object On
  object Never
  object CalledAgain
}

trait IdiomaticMockitoBase extends MockitoEnhancer {

  import IdiomaticMockitoBase._

  type Verification

  def verification(v: => Any): Verification

  implicit class StubbingOps[T](stubbing: => T) {

    def shouldReturn: ReturnActions[T] = macro WhenMacro.shouldReturn[T]

    def shouldCall(crm: RealMethod.type): ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]

    def shouldThrow: ThrowActions[T] = macro WhenMacro.shouldThrow[T]

    def shouldAnswer: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]

    def was(called: Called.type)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacro[T, Verification]

    def wasNever(called: Called.type)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasNotMacro[T, Verification]

    def wasNever(called: CalledAgain.type)(implicit $ev: T <:< AnyRef): Verification =
      verification(verifyNoMoreInteractions(stubbing.asInstanceOf[AnyRef]))

    def wasCalled(t: Times)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacroTimes[T, Verification]

    def wasCalled(t: AtLeast)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacroAtLeast[T, Verification]

    def wasCalled(t: AtMost)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacroAtMost[T, Verification]

    def wasCalled(t: OnlyOn.type)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacroOnlyOn[T, Verification]

    //noinspection AccessorLikeMethodIsUnit
    def isLenient(): Unit = macro WhenMacro.isLenient[T]
  }

  val called: Called.type            = Called
  val thrown: Thrown.type            = Thrown
  val returned: Returned.type        = Returned
  val answered: Answered.type        = Answered
  val theRealMethod: RealMethod.type = RealMethod

  implicit class DoSomethingOps[R](v: R) {
    def willBe(r: Returned.type): ReturnedBy.type = ReturnedBy
    def willBe(a: Answered.type): AnsweredBy.type = AnsweredBy
  }

  implicit class ThrowSomethingOps[R <: Throwable](v: R) {
    def willBe(thrown: Thrown.type): ThrownBy.type = ThrownBy
  }

  val calledAgain: CalledAgain.type = CalledAgain

  val realMethod: RealMethod.type = RealMethod

  val on: On.type                = On
  val onlyHere: OnlyOn.type      = OnlyOn
  val once: Times                = Times(1)
  val twice: Times               = Times(2)
  val thrice: Times              = Times(3)
  val threeTimes: Times          = Times(3)
  val fourTimes: Times           = Times(4)
  val fiveTimes: Times           = Times(5)
  val sixTimes: Times            = Times(6)
  val sevenTimes: Times          = Times(7)
  val eightTimes: Times          = Times(8)
  val nineTimes: Times           = Times(9)
  val tenTimes: Times            = Times(10)
  val atLeastOnce: AtLeast       = AtLeast(1)
  val atLeastTwice: AtLeast      = AtLeast(2)
  val atLeastThrice: AtLeast     = AtLeast(3)
  val atLeastThreeTimes: AtLeast = AtLeast(3)
  val atLeastFourTimes: AtLeast  = AtLeast(4)
  val atLeastFiveTimes: AtLeast  = AtLeast(5)
  val atLeastSixTimes: AtLeast   = AtLeast(6)
  val atLeastSevenTimes: AtLeast = AtLeast(7)
  val atLeastEightTimes: AtLeast = AtLeast(8)
  val atLeastNineTimes: AtLeast  = AtLeast(9)
  val atLeastTenTimes: AtLeast   = AtLeast(10)
  val atMostOnce: AtMost         = AtMost(1)
  val atMostTwice: AtMost        = AtMost(2)
  val atMostThrice: AtMost       = AtMost(3)
  val atMostThreeTimes: AtMost   = AtMost(3)
  val atMostFourTimes: AtMost    = AtMost(4)
  val atMostFiveTimes: AtMost    = AtMost(5)
  val atMostSixTimes: AtMost     = AtMost(6)
  val atMostSevenTimes: AtMost   = AtMost(7)
  val atMostEightTimes: AtMost   = AtMost(8)
  val atMostNineTimes: AtMost    = AtMost(9)
  val atMostTenTimes: AtMost     = AtMost(10)

  def InOrder(mocks: AnyRef*)(verifications: VerifyInOrder => Unit): Verification = verification(verifications(VerifyInOrder(mocks)))

  def atLeast(t: Times): AtLeast = AtLeast(t.times)
  def atMost(t: Times): AtMost   = AtMost(t.times)

  implicit class IntOps(i: Int) {
    def times: Times = Times(i)
  }
}
