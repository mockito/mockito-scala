package org.mockito
package cats

import _root_.cats.{ Applicative, ApplicativeError }
import org.mockito.internal.ValueClassWrapper
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

import scala.reflect.ClassTag

case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) {

  def thenReturn(value: T)(implicit F: Applicative[F]): CatsStubbing[F, T] = delegate thenReturn F.pure(value)
  def andThen(value: T)(implicit F: Applicative[F]): CatsStubbing[F, T]    = thenReturn(value)
  def andThen(value: F[T]): CatsStubbing[F, T]                             = delegate thenReturn value

  def thenAnswer(f: => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = delegate thenAnswer invocationToAnswer(_ => f).andThen(F.pure)
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F]): CatsStubbing[F, T] =
    clazz[P0] match {
      case c if c == classOf[InvocationOnMock] => delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.pure)
      case _                                   => delegate thenAnswer functionToAnswer(f).andThen(F.pure)
    }
  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper](f: (P0, P1) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper](f: (P0, P1, P2) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper](
      f: (P0, P1, P2, P3) => T
  )(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4) => T
  )(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper, P5: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4, P5) => T
  )(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper, P5: ValueClassWrapper, P6: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4, P5, P6) => T
  )(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

//  (2 to 22).foreach { fn =>
//    val args = 0 until fn
//    print(s"""
//             |def thenAnswer[${args.map(a => s"P$a: ValueClassWrapper").mkString(",")}](f: (${args.map(a => s"P$a").mkString(",")}) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
//             |    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
//             |""".stripMargin)
//  }

  def thenFailWith[E](error: E)(implicit F: ApplicativeError[F, _ >: E]): CatsStubbing[F, T] =
    delegate thenReturn F.raiseError[T](error)

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing {
  implicit def toCatsStubbing[F[_], T](v: OngoingStubbing[F[T]]): CatsStubbing[F, T] = CatsStubbing(v)

  implicit def toMock[F[_], T, M](s: CatsStubbing[F, T]): M = s.getMock[M]
}

case class CatsStubbing2[F[_], G[_], T](delegate: OngoingStubbing[F[G[T]]]) {

  def thenReturn(value: T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenReturn F.compose[G].pure(value)
  def andThen(value: T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = thenReturn(value)
  def andThen(value: G[T])(implicit F: Applicative[F]): CatsStubbing2[F, G, T]                 = delegate thenReturn F.pure(value)
  def andThen(value: F[G[T]]): CatsStubbing2[F, G, T]                                          = delegate thenReturn value

  def thenAnswer(f: => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer invocationToAnswer(_ => f).andThen(F.compose[G].pure)
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    clazz[P0] match {
      case c if c == classOf[InvocationOnMock] =>
        delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.compose[G].pure)
      case _ => delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
    }
  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper](f: (P0, P1) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper](f: (P0, P1, P2) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper](
      f: (P0, P1, P2, P3) => T
  )(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4) => T
  )(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper, P5: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4, P5) => T
  )(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper, P5: ValueClassWrapper, P6: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4, P5, P6) => T
  )(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) => T
  )(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenAnswer[
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
  ](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) => T
  )(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

//  (2 to 22).foreach { fn =>
//    val args = 0 until fn
//    print(s"""
//             |def thenAnswer[${args.map(a => s"P$a: ValueClassWrapper").mkString(",")}](f: (${args.map(a => s"P$a").mkString(",")}) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
//             |    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
//             |""".stripMargin)
//  }

  def thenFailWith[E](error: E)(implicit ae: Applicative[F], ag: ApplicativeError[G, _ >: E]): CatsStubbing2[F, G, T] =
    delegate thenReturn ae.pure(ag.raiseError[T](error))

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing2 {
  implicit def toCatsStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): CatsStubbing2[F, G, T] = CatsStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: CatsStubbing2[F, G, T]): M = s.getMock[M]
}
