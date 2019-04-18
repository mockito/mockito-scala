package org.mockito

import org.mockito.WhenMacro._
import org.mockito.stubbing.ScalaOngoingStubbing
import org.mockito.verification.VerificationMode

import scala.concurrent.duration.Duration

object IdiomaticMockitoBase {
  object Returned
  case class ReturnedBy[T]() {
    def by[S](stubbing: S)(implicit $ev: T <:< S): S = macro DoSomethingMacro.returnedBy[T, S]
  }

  object Answered
  case class AnsweredBy[T]() {
    def by[S](stubbing: S)(implicit $ev: T <:< S): S = macro DoSomethingMacro.answeredBy[T, S]
  }

  object Thrown
  object ThrownBy {
    def by[T](stubbing: T): T = macro DoSomethingMacro.thrownBy[T]
  }

  object On
  object Never
  sealed trait CalledAgain
  object IgnoringStubs
  case object CalledAgain extends CalledAgain {
    def apply(i: IgnoringStubs.type): CalledAgain = LenientCalledAgain
  }
  case object LenientCalledAgain extends CalledAgain

  case class Times(times: Int) extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.times(times)
    def within(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).times(times)
    }
  }

  //Helper methods for the specs2 macro
  def Exactly(times: Int): Times = Times(times)
  def AtLeastOne: AtLeast        = AtLeast(1)
  def AtLeastTwo: AtLeast        = AtLeast(2)
  def AtLeastThree: AtLeast      = AtLeast(3)
  def AtMostOne: AtMost          = AtMost(1)
  def AtMostTwo: AtMost          = AtMost(2)
  def AtMostThree: AtMost        = AtMost(3)

  case class AtLeast(times: Int) extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.atLeast(times)
    def within(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).atLeast(times)
    }
  }

  case class AtMost(times: Int) extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.atMost(times)
  }

  object OnlyOn extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.only
    def within(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).only
    }
  }
}

trait IdiomaticMockitoBase extends MockitoEnhancer {

  import IdiomaticMockitoBase._

  type Verification

  def verification(v: => Any): Verification

  implicit class StubbingOps[T](stubbing: T) {

    def shouldReturn: ReturnActions[T] = macro WhenMacro.shouldReturn[T]
    def mustReturn: ReturnActions[T] = macro WhenMacro.shouldReturn[T]
    def returns: ReturnActions[T] = macro WhenMacro.shouldReturn[T]

    def shouldCall(crm: RealMethod.type): ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]
    def mustCall(crm: RealMethod.type): ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]
    def calls(crm: RealMethod.type): ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]

    def shouldThrow: ThrowActions[T] = macro WhenMacro.shouldThrow[T]
    def mustThrow: ThrowActions[T] = macro WhenMacro.shouldThrow[T]
    def throws: ThrowActions[T] = macro WhenMacro.shouldThrow[T]

    def shouldAnswer: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]
    def mustAnswer: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]
    def answers: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]

    def was(called: Called.type)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacro[T, Verification]

    def wasNever(called: Called.type)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasNotMacro[T, Verification]

    def wasNever(called: CalledAgain)(implicit $ev: T <:< AnyRef): Verification = called match {
      case CalledAgain        => verification(verifyNoMoreInteractions(stubbing.asInstanceOf[AnyRef]))
      case LenientCalledAgain => verification(verifyNoMoreInteractions(ignoreStubs(stubbing.asInstanceOf[AnyRef]): _*))
    }

    def wasCalled(t: ScalaVerificationMode)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasCalledMacro[T, Verification]

    //noinspection AccessorLikeMethodIsUnit
    def isLenient(): Unit = macro WhenMacro.isLenient[T]
  }

  val called: Called.type            = Called
  val thrown: Thrown.type            = Thrown
  val returned: Returned.type        = Returned
  val answered: Answered.type        = Answered
  val theRealMethod: RealMethod.type = RealMethod

  implicit class DoSomethingOps[R](v: R) {
    def willBe(r: Returned.type): ReturnedBy[R] = ReturnedBy[R]()
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }

  implicit class DoSomethingOps0[R](v: () => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps1[P0, R](v: P0 => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps2[P0, P1, R](v: (P0, P1) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps3[P0, P1, P2, R](v: (P0, P1, P2) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps4[P0, P1, P2, P3, R](v: (P0, P1, P2, P3) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps5[P0, P1, P2, P3, P4, R](v: (P0, P1, P2, P3, P4) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps6[P0, P1, P2, P3, P4, P5, R](v: (P0, P1, P2, P3, P4, P5) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps7[P0, P1, P2, P3, P4, P5, P6, R](v: (P0, P1, P2, P3, P4, P5, P6) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps8[P0, P1, P2, P3, P4, P5, P6, P7, R](v: (P0, P1, P2, P3, P4, P5, P6, P7) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps9[P0, P1, P2, P3, P4, P5, P6, P7, P8, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps10[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }

  implicit class ThrowSomethingOps[R <: Throwable](v: R) {
    def willBe(thrown: Thrown.type): ThrownBy.type = ThrownBy
  }

  val calledAgain: CalledAgain.type     = CalledAgain
  val ignoringStubs: IgnoringStubs.type = IgnoringStubs

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

  def InOrder(mocks: AnyRef*)(verifications: VerifyInOrder => Verification): Verification = verifications(VerifyInOrder(mocks))

  def atLeast(t: Times): AtLeast = AtLeast(t.times)
  def atMost(t: Times): AtMost   = AtMost(t.times)

  implicit class IntOps(i: Int) {
    def times: Times = Times(i)
  }
}
