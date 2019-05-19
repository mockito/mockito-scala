package org.mockito.cats

import cats.implicits._
import cats.{ Applicative, ApplicativeError }
import org.mockito.stubbing.OngoingStubbing

case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) {

  def thenReturn(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T] = delegate thenReturn value.pure[F]
  def andThen(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T]    = thenReturn(value)
  def andThen(value: F[T]): CatsStubbing[F, T]                             = delegate thenReturn value

  def thenFailWith[E](error: E)(implicit ae: ApplicativeError[F, _ >: E]): CatsStubbing[F, T] = delegate thenReturn error.raiseError[F, T]

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing {
  implicit def toCatsStubbing[F[_], T](v: OngoingStubbing[F[T]]): CatsStubbing[F, T] = CatsStubbing(v)

  implicit def toMock[F[_], T, M](s: CatsStubbing[F, T]): M = s.getMock[M]
}

case class CatsStubbing2[F[_], G[_], T](delegate: OngoingStubbing[F[G[T]]]) {

  def thenReturn(value: T)(implicit af: Applicative[F], ag: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenReturn af.compose[G].pure(value)
  def andThen(value: T)(implicit af: Applicative[F], ag: Applicative[G]): CatsStubbing2[F, G, T] = thenReturn(value)
  def andThen(value: G[T])(implicit af: Applicative[F]): CatsStubbing2[F, G, T]                  = delegate thenReturn af.pure(value)
  def andThen(value: F[G[T]]): CatsStubbing2[F, G, T]                                            = delegate thenReturn value

  def thenFailWith[E](error: E)(implicit ae: Applicative[F], ag: ApplicativeError[G, _ >: E]): CatsStubbing2[F, G, T] =
    delegate thenReturn ae.pure(ag.raiseError[T](error))

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing2 {
  implicit def toCatsStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): CatsStubbing2[F, G, T] = CatsStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: CatsStubbing2[F, G, T]): M = s.getMock[M]
}
