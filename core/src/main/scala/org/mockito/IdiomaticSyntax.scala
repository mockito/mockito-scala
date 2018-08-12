package org.mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

import scala.reflect.ClassTag

trait IdiomaticSyntax { self: MockitoSugar with ArgumentMatchersSugar =>

  implicit class StubbingOps[T](stubbing: => T) {

    def shouldReturn(value: T): OngoingStubbing[T] = when(stubbing) thenReturn value

    def shouldCallRealMethod: OngoingStubbing[T] = when(stubbing) thenCallRealMethod ()

    def shouldThrow[E <: Throwable: ClassTag]: OngoingStubbing[T] = when(stubbing) thenThrow clazz

    def shouldThrow[E <: Throwable](e: E): OngoingStubbing[T] = when(stubbing) thenThrow e

    def shouldAnswer(f: InvocationOnMock => T): OngoingStubbing[T] = when(stubbing) thenAnswer (f(_))
  }

  //noinspection UnitMethodIsParameterless
  implicit class VerificationOps[T <: AnyRef](mock: T) {

    def wasNotUsed: Unit = verifyZeroInteractions(mock)

    def wasNotUsedAgain: Unit = verifyNoMoreInteractions(mock)

    def wasCalledOn: T = verify(mock)

    def wasCalledOn[R](f: T => R): CalledPostFix[T, R] = CalledPostFix(mock, f)

    def wasCalled: CalledPreFix[T] = CalledPreFix(mock)

    def wasNeverCalledOn: T = verify(mock, never)

    def wasNeverCalledOn[R](f: T => R) = f(verify(mock, never))

    def wasOnlyCalledOn: T = verify(mock, only)

    def wasOnlyCalledOn[R](f: T => R) = f(verify(mock, only))
  }

  implicit class InvocationOnMockOps(i: InvocationOnMock) {
    def arg0[T]: T  = i.getArgument[T](0)
    def arg1[T]: T  = i.getArgument[T](1)
    def arg2[T]: T  = i.getArgument[T](2)
    def arg3[T]: T  = i.getArgument[T](3)
    def arg4[T]: T  = i.getArgument[T](4)
    def arg5[T]: T  = i.getArgument[T](5)
    def arg6[T]: T  = i.getArgument[T](6)
    def arg7[T]: T  = i.getArgument[T](7)
    def arg8[T]: T  = i.getArgument[T](8)
    def arg9[T]: T  = i.getArgument[T](9)
    def arg10[T]: T = i.getArgument[T](10)
  }

  case class CalledPostFix[T <: AnyRef, R](mock: T, f: T => R) {
    private def verifyTimes(t: Int): R        = f(verify(mock, times(t)))
    private def verifyAtLeastTimes(t: Int): R = f(verify(mock, atLeast(t)))
    private def verifyAtMostTimes(t: Int): R  = f(verify(mock, atMost(t)))

    def once: R       = verifyTimes(1)
    def twice: R      = verifyTimes(2)
    def thrice: R     = verifyTimes(3)
    def threeTimes: R = verifyTimes(3)
    def fourTimes: R  = verifyTimes(4)
    def fiveTimes: R  = verifyTimes(5)
    def sixTimes: R   = verifyTimes(6)
    def sevenTimes: R = verifyTimes(7)
    def eightTimes: R = verifyTimes(8)
    def nineTimes: R  = verifyTimes(9)
    def tenTimes: R   = verifyTimes(10)

    def atLeastOnce: R       = verifyAtLeastTimes(1)
    def atLeastTwice: R      = verifyAtLeastTimes(2)
    def atLeastThrice: R     = verifyAtLeastTimes(3)
    def atLeastThreeTimes: R = verifyAtLeastTimes(3)
    def atLeastFourTimes: R  = verifyAtLeastTimes(4)
    def atLeastFiveTimes: R  = verifyAtLeastTimes(5)
    def atLeastSixTimes: R   = verifyAtLeastTimes(6)
    def atLeastSevenTimes: R = verifyAtLeastTimes(7)
    def atLeastEightTimes: R = verifyAtLeastTimes(8)
    def atLeastNineTimes: R  = verifyAtLeastTimes(9)
    def atLeastTenTimes: R   = verifyAtLeastTimes(10)

    def atMostOnce: R       = verifyAtMostTimes(1)
    def atMostTwice: R      = verifyAtMostTimes(2)
    def atMostThrice: R     = verifyAtMostTimes(3)
    def atMostThreeTimes: R = verifyAtMostTimes(3)
    def atMostFourTimes: R  = verifyAtMostTimes(4)
    def atMostFiveTimes: R  = verifyAtMostTimes(5)
    def atMostSixTimes: R   = verifyAtMostTimes(6)
    def atMostSevenTimes: R = verifyAtMostTimes(7)
    def atMostEightTimes: R = verifyAtMostTimes(8)
    def atMostNineTimes: R  = verifyAtMostTimes(9)
    def atMostTenTimes: R   = verifyAtMostTimes(10)
  }

  case class CalledPreFix[T <: AnyRef](mock: T) {
    private def verifyTimes(t: Int): T        = verify(mock, times(t))
    private def verifyAtLeastTimes(t: Int): T = verify(mock, atLeast(t))
    private def verifyAtMostTimes(t: Int): T  = verify(mock, atMost(t))

    def onceOn: T       = verifyTimes(1)
    def twiceOn: T      = verifyTimes(2)
    def thriceOn: T     = verifyTimes(3)
    def threeTimesOn: T = verifyTimes(3)
    def fourTimesOn: T  = verifyTimes(4)
    def fiveTimesOn: T  = verifyTimes(5)
    def sixTimesOn: T   = verifyTimes(6)
    def sevenTimesOn: T = verifyTimes(7)
    def eightTimesOn: T = verifyTimes(8)
    def nineTimesOn: T  = verifyTimes(9)
    def tenTimesOn: T   = verifyTimes(10)

    def atLeastOnceOn: T       = verifyAtLeastTimes(1)
    def atLeastTwiceOn: T      = verifyAtLeastTimes(2)
    def atLeastThreeTimesOn: T = verifyAtLeastTimes(3)
    def atLeastFourTimesOn: T  = verifyAtLeastTimes(4)
    def atLeastFiveTimesOn: T  = verifyAtLeastTimes(5)
    def atLeastSixTimesOn: T   = verifyAtLeastTimes(6)
    def atLeastSevenTimesOn: T = verifyAtLeastTimes(7)
    def atLeastEightTimesOn: T = verifyAtLeastTimes(8)
    def atLeastNineTimesOn: T  = verifyAtLeastTimes(9)
    def atLeastTenTimesOn: T   = verifyAtLeastTimes(10)

    def atMostOnceOn: T       = verifyAtMostTimes(1)
    def atMostTwiceOn: T      = verifyAtMostTimes(2)
    def atMostThreeTimesOn: T = verifyAtMostTimes(3)
    def atMostFourTimesOn: T  = verifyAtMostTimes(4)
    def atMostFiveTimesOn: T  = verifyAtMostTimes(5)
    def atMostSixTimesOn: T   = verifyAtMostTimes(6)
    def atMostSevenTimesOn: T = verifyAtMostTimes(7)
    def atMostEightTimesOn: T = verifyAtMostTimes(8)
    def atMostNineTimesOn: T  = verifyAtMostTimes(9)
    def atMostTenTimesOn: T   = verifyAtMostTimes(10)
  }

  def *[T]: T = any[T]
}
