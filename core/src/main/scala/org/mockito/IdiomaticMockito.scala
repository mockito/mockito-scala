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

  override def spy[T](realObj: T): T = MockitoSugar.spy(realObj)

  override def spyLambda[T <: AnyRef: ClassTag](realObj: T): T = MockitoSugar.spyLambda(realObj)

  implicit class StubbingOps[T](stubbing: => T) {

    def shouldReturn(value: T): OngoingStubbing[T] = when(stubbing) thenReturn value

    def shouldCallRealMethod: OngoingStubbing[T] = when(stubbing) thenCallRealMethod ()

    def shouldThrow[E <: Throwable: ClassTag]: OngoingStubbing[T] = when(stubbing) thenThrow clazz

    def shouldThrow[E <: Throwable](e: E): OngoingStubbing[T] = when(stubbing) thenThrow e

    def shouldAnswer(f: => T): OngoingStubbing[T] = when(stubbing) thenAnswer invocationToAnswer(_ => f)

    def shouldAnswer[P0](f: P0 => T): OngoingStubbing[T] =
      when(stubbing) thenAnswer functionToAnswer(f)

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

  implicit class OngoingStubbingOps[T](ongoingStubbing: OngoingStubbing[T]) {

    def andThen(value: T): OngoingStubbing[T] = ongoingStubbing thenReturn value

    def andThenCallRealMethod: OngoingStubbing[T] = ongoingStubbing thenCallRealMethod ()

    def andThen[E <: Throwable](e: E): OngoingStubbing[T] = ongoingStubbing thenThrow e

    def andThenAnswer(f: => T): OngoingStubbing[T] = ongoingStubbing thenAnswer invocationToAnswer(_ => f)

    def andThenAnswer[P0](f: P0 => T): OngoingStubbing[T] =
      ongoingStubbing thenAnswer functionToAnswer(f)

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

    def andThenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): OngoingStubbing[T] =
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

  implicit class VerificationOps[T <: AnyRef](mock: T)(implicit order: Option[InOrder] = None) {

    def wasCalled(on: On): T = order.fold(verify(mock))(_.verify(mock))

    def wasCalled(t: Times): T = order.fold(verify(mock, times(t.times)))(_.verify(mock, times(t.times)))

    def wasCalled(onlyOn: OnlyOn): T = order.fold(verify(mock, only))(_.verify(mock, only))

    def was(n: Never): NeverInstance[T] = NeverInstance(mock)
  }

  object InOrder {
    def apply(mocks: AnyRef*)(verifications: Option[InOrder] => Unit): Unit = verifications(Some(Mockito.inOrder(mocks: _*)))
  }

  def *[T]: T = ArgumentMatchersSugar.any[T]
}
