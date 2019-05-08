package org.mockito.cats

import cats.implicits._
import cats.{ Applicative, ApplicativeError }
import org.mockito.stubbing.OngoingStubbing

case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) {

  def thenReturn(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T] = delegate.thenReturn(value.pure[F])

  def thenFailWith[E](error: E)(implicit ae: ApplicativeError[F, E]): CatsStubbing[F, T] = delegate.thenReturn(error.raiseError[F, T])

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing {
  implicit def toCatsFirstStubbing[F[_], T](v: OngoingStubbing[F[T]]): CatsStubbing[F, T] = CatsStubbing(v)
}
