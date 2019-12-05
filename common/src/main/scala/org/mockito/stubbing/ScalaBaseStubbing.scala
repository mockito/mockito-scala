package org.mockito
package stubbing

import org.mockito.internal.stubbing.answers.ScalaThrowsException
import org.mockito.internal.{ ValueClassExtractor, ValueClassWrapper }
import org.mockito.invocation.InvocationOnMock
import org.objenesis.ObjenesisStd

import scala.reflect.ClassTag

abstract class ScalaBaseStubbing[T: ValueClassExtractor] {
  protected def delegate: OngoingStubbing[T]

  protected def _thenReturn(value: T, values: Seq[T]): ScalaOngoingStubbing[T] =
    values.foldLeft(delegate.thenAnswer(ScalaReturns(value))) {
      case (s, v) => s.thenAnswer(ScalaReturns(v))
    }

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
  protected def _thenAnswer[P0: ClassTag: ValueClassWrapper](f: P0 => T): ScalaOngoingStubbing[T] = clazz[P0] match {
    case c if c == classOf[InvocationOnMock] => delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0]))
    case _                                   => delegate thenAnswer functionToAnswer(f)
  }
  protected def _thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper](f: (P0, P1) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper](f: (P0, P1, P2) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper](f: (P0, P1, P2, P3) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4) => T
  ): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper, P5: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4, P5) => T
  ): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper,
      P20: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

  protected def _thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper,
      P20: ValueClassWrapper,
      P21: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) => T): ScalaOngoingStubbing[T] =
    delegate thenAnswer functionToAnswer(f)

//    (2 to 22).foreach { fn =>
//      val args = 0 until fn
//      print(s"""
//               |protected def _thenAnswer[${args.map(a => s"P$a: ValueClassWrapper").mkString(",")}](f: (${args.map(a => s"P$a").mkString(",")}) => T): ScalaOngoingStubbing[T] =
//               |    delegate thenAnswer functionToAnswer(f)
//               |""".stripMargin)
//    }

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
