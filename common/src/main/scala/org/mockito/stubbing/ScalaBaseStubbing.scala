package org.mockito.stubbing

import org.mockito.internal.ValueClassExtractor
import org.mockito.internal.stubbing.answers.ScalaThrowsException
import org.mockito.invocation.InvocationOnMock
import org.mockito.{ clazz, functionToAnswer, invocationToAnswer }
import org.objenesis.ObjenesisStd

import scala.reflect.ClassTag

trait ScalaBaseStubbing[T] {

  protected def delegate: OngoingStubbing[T]
  protected implicit def $vce: ValueClassExtractor[T]

  protected def _thenReturn(value: T, values: Seq[T]): ScalaOngoingStubbing[T] =
    delegate.thenReturn($vce.extractAs[T](value), values.map($vce.extractAs[T]): _*)

  private def thenThrow(t: Throwable): ScalaOngoingStubbing[T] = delegate thenAnswer new ScalaThrowsException(t)

  protected def _thenThrow(throwables: Seq[Throwable]): ScalaOngoingStubbing[T] =
    if (throwables == null || throwables.isEmpty) thenThrow(null)
    else
      throwables.tail.foldLeft(thenThrow(throwables.head)) {
        case (os, t) => os andThenThrow t
      }

  protected def _thenThrow[E <: Throwable: ClassTag]: ScalaOngoingStubbing[T] = thenThrow((new ObjenesisStd).newInstance(clazz))

  protected def _thenCallRealMethod(): ScalaOngoingStubbing[T] = delegate.thenCallRealMethod()

  protected def _thenAnswer(f: => T): ScalaOngoingStubbing[T] = delegate thenAnswer invocationToAnswer(_ => f)
  protected def _thenAnswer[P0: ClassTag](f: P0 => T): ScalaOngoingStubbing[T] = clazz[P0] match {
    case c if c == classOf[InvocationOnMock] => delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0]))
    case _                                   => delegate thenAnswer functionToAnswer(f)
  }
  protected def _thenAnswer[P0, P1](f: (P0, P1) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2](f: (P0, P1, P2) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)
  protected def _thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  /**
   * Returns the mock that was used for this stub.
   * <p>
   * It allows to create a stub in one line of code.
   * This can be helpful to keep test code clean.
   * For example, some boring stub can be created & stubbed at field initialization in a test:
   * <pre class="code"><code class="java">
   * public class CarTest {
   *   Car boringStubbedCar = when(mock(Car.class).shiftGear()).thenThrow(EngineNotStarted.class).getMock();
   *
   *   &#064;Test public void should... {}
   * </code></pre>
   *
   * @param <M> The mock type given by the variable type.
   * @return Mock used in this ongoing stubbing.
   */
  def getMock[M]: M = delegate.getMock[M]
}
