package org.mockito
package cats

import _root_.cats.{ Applicative, ApplicativeError }
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

import scala.reflect.ClassTag

case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) {

  def thenReturn(value: T)(implicit F: Applicative[F]): CatsStubbing[F, T] = delegate thenReturn F.pure(value)
  def andThen(value: T)(implicit F: Applicative[F]): CatsStubbing[F, T]    = thenReturn(value)
  def andThen(value: F[T]): CatsStubbing[F, T]                             = delegate thenReturn value

  def thenAnswer(f: => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = delegate thenAnswer invocationToAnswer(_ => f).andThen(F.pure)
  def thenAnswer[P0: ClassTag](f: P0 => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = clazz[P0] match {
    case c if c == classOf[InvocationOnMock] => delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.pure)
    case _                                   => delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  }
  def thenAnswer[P0, P1](f: (P0, P1) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2](f: (P0, P1, P2) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(
      implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(
      implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T)(
      implicit F: Applicative[F]): CatsStubbing[F, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.pure)

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
  def thenAnswer[P0: ClassTag](f: P0 => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = clazz[P0] match {
    case c if c == classOf[InvocationOnMock] =>
      delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.compose[G].pure)
    case _ => delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  }
  def thenAnswer[P0, P1](f: (P0, P1) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2](f: (P0, P1, P2) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T)(implicit F: Applicative[F],
                                                                           G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T)(implicit F: Applicative[F],
                                                                                   G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F],
                                                                                           G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
  def thenAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)

  def thenFailWith[E](error: E)(implicit ae: Applicative[F], ag: ApplicativeError[G, _ >: E]): CatsStubbing2[F, G, T] =
    delegate thenReturn ae.pure(ag.raiseError[T](error))

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing2 {
  implicit def toCatsStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): CatsStubbing2[F, G, T] = CatsStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: CatsStubbing2[F, G, T]): M = s.getMock[M]
}
