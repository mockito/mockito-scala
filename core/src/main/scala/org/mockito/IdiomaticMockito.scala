package org.mockito

import org.mockito.stubbing.{ DefaultAnswer, ScalaOngoingStubbing }
import org.mockito.MockitoSugar._
import org.mockito.VerifyMacro._
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

    def shouldCall(crm: RealMethod): ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]

    def shouldThrow: ThrowActions[T] = macro WhenMacro.shouldThrow[T]

    def shouldAnswer: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]

    def was(called: Called.type)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacro[T]

    def wasNever(called: Called.type)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasNotMacro[T]

    def wasNever(called: CalledAgain)(implicit $ev: T <:< AnyRef): Unit = verifyNoMoreInteractions(stubbing.asInstanceOf[AnyRef])

    def wasCalled(t: Times)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacroTimes[T]

    def wasCalled(t: AtLeast)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacroAtLeast[T]

    def wasCalled(t: AtMost)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacroAtMost[T]

    def wasCalled(t: OnlyOn)(implicit order: VerifyOrder): Unit = macro VerifyMacro.wasMacroOnlyOn[T]

  }

  class Returned
  object ReturnedBy {
    def by[T](stubbing: T): T = macro DoSomethingMacro.returnedBy[T]
  }

  class Answered
  object AnsweredBy {
    def by[T](stubbing: T): T = macro DoSomethingMacro.answeredBy[T]
  }

  object RealMethod {
    def willBe(called: Called.type): Called.type = called
  }

  class Thrown
  object ThrownBy {
    def by[T](stubbing: T): T = macro DoSomethingMacro.thrownBy[T]
  }

  val called: Called.type            = Called
  val thrown: Thrown                 = new Thrown
  val returned: Returned             = new Returned
  val answered: Answered             = new Answered
  val theRealMethod: RealMethod.type = RealMethod

  implicit class DoSomethingOps[R](v: R) {
    def willBe(r: Returned): ReturnedBy.type = ReturnedBy
    def willBe(a: Answered): AnsweredBy.type = AnsweredBy
  }

  implicit class ThrowSomethingOps[R <: Throwable](v: R) {
    def willBe(thrown: Thrown): ThrownBy.type = ThrownBy
  }

  class On
  class Never
  class CalledAgain

  val calledAgain = new CalledAgain

  val realMethod = new RealMethod

  val on                = new On
  val onlyHere          = new OnlyOn
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

  object InOrder {
    def apply(mocks: AnyRef*)(verifications: VerifyInOrder => Unit): Unit = verifications(VerifyInOrder(mocks))
  }
}

/**
 * Simple object to allow the usage of the trait without mixing it in
 */
object IdiomaticMockito extends IdiomaticMockito
