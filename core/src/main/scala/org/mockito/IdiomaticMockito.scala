package org.mockito

import org.mockito.stubbing.{ Answer, OngoingStubbing }
import MockitoSugar.{ verify, _ }

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

trait IdiomaticMockito extends MockCreator {

  override def mock[T <: AnyRef: ClassTag: TypeTag](name: String): T = MockitoSugar.mock[T](name)

  override def mock[T <: AnyRef: ClassTag: TypeTag](mockSettings: MockSettings): T = MockitoSugar.mock[T](mockSettings)

  override def mock[T <: AnyRef: ClassTag: TypeTag](defaultAnswer: Answer[_]): T = MockitoSugar.mock[T](defaultAnswer)

  override def mock[T <: AnyRef: ClassTag: TypeTag]: T = MockitoSugar.mock[T]

  implicit class StubbingOps[T](stubbing: => T) {

    def shouldReturn(value: T): OngoingStubbing[T] = when(stubbing) thenReturn value

    def shouldCallRealMethod: OngoingStubbing[T] = when(stubbing) thenCallRealMethod ()

    def shouldThrow[E <: Throwable: ClassTag]: OngoingStubbing[T] = when(stubbing) thenThrow clazz

    def shouldThrow[E <: Throwable](e: E): OngoingStubbing[T] = when(stubbing) thenThrow e

    def shouldAnswer(f: => T): OngoingStubbing[T] = when(stubbing) thenAnswer functionToAnswer(_ => f)

    def shouldAnswer[P0](f: P0 => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer function1ToAnswer(f)

    def shouldAnswer[P0, P1](f: (P0, P1) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2](f: (P0, P1, P2) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

    def shouldAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

  }

  implicit class OngoingStubbingOps[T](ongoingStubbing: OngoingStubbing[T]) {

    def andThen(value: T): OngoingStubbing[T] = ongoingStubbing thenReturn value

    def andThenCallRealMethod: OngoingStubbing[T] = ongoingStubbing thenCallRealMethod ()

    def andThen[E <: Throwable](e: E): OngoingStubbing[T] = ongoingStubbing thenThrow e

    def andThenAnswer(f: => T): OngoingStubbing[T] = ongoingStubbing thenAnswer functionToAnswer(_ => f)

    def andThenAnswer[P0](f: P0 => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer function1ToAnswer(f)

    def andThenAnswer[P0, P1](f: (P0, P1) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2](f: (P0, P1, P2) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

    def andThenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)
  }

  class On
  class OnlyOn
  class Never
  //noinspection UnitMethodIsParameterless
  case class NeverInstance[T <: AnyRef](mock: T) {
    def called: Unit               = verifyZeroInteractions(mock)
    def called(on: On): T          = verify(mock, MockitoSugar.never)
    def called(again: Again): Unit = verifyNoMoreInteractions(mock)
  }
  class Again
  case class Times(times: Int)

  val on           = new On
  val onlyOn       = new OnlyOn
  val never        = new Never
  val again        = new Again
  val onceOn       = Times(1)
  val twiceOn      = Times(2)
  val thriceOn     = Times(3)
  val threeTimesOn = Times(3)
  val fourTimesOn  = Times(4)
  val fiveTimesOn  = Times(5)
  val sixTimesOn   = Times(6)
  val sevenTimesOn = Times(7)
  val eightTimesOn = Times(8)
  val nineTimesOn  = Times(9)
  val tenTimesOn   = Times(10)

  implicit class VerificationOps[T <: AnyRef](mock: T) {

    def wasCalled(on: On): T = verify(mock)

    def wasCalled(t: Times): T = verify(mock, times(t.times))

    def wasCalled(onlyOn: OnlyOn): T = verify(mock, only)

    def was(n: Never): NeverInstance[T] = NeverInstance(mock)

  }

  def *[T]: T = ArgumentMatchersSugar.any[T]
}
