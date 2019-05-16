package org.mockito.cats

import cats.implicits._
import cats.{ Applicative, ApplicativeError }
import org.mockito.stubbing.OngoingStubbing

case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) {

  def thenReturn(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T] = delegate thenReturn value.pure[F]

  def thenFailWith[E](error: E)(implicit ae: ApplicativeError[F, E]): CatsStubbing[F, T] = delegate thenReturn error.raiseError[F, T]

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing {
  implicit def toCatsStubbing[F[_], T](v: OngoingStubbing[F[T]]): CatsStubbing[F, T] = CatsStubbing(v)

  implicit def toMock[F[_], T, M](s: CatsStubbing[F, T]): M = s.getMock[M]
}

case class CatsStubbing2[F[_], G[_], T](delegate: OngoingStubbing[F[G[T]]]) {

  def thenReturn(value: T)(implicit af: Applicative[F], ag: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenReturn Applicative[F].compose[G].pure(value)

  def thenFailWith[E](error: E)(implicit ae: Applicative[F], ag: ApplicativeError[G, E]): CatsStubbing2[F, G, T] =
    delegate thenReturn Applicative[F].pure(ApplicativeError[G, E].raiseError[T](error))

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing2 {
  implicit def toCatsStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): CatsStubbing2[F, G, T] = CatsStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: CatsStubbing2[F, G, T]): M = s.getMock[M]
}
