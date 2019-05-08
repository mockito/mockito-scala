package org.mockito.cats

import cats.{Applicative, ApplicativeError}
import org.mockito._
import org.mockito.stubbing.OngoingStubbing

case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) {

  def thenReturn(value: T)(implicit F: Applicative[F]): CatsStubbing[F, T] = {
    val wrapped: F[T] = F.pure(value)
    delegate.thenReturn(wrapped)
  }

  def thenFailWith[E](error: E)(implicit F: ApplicativeError[F, E]): CatsStubbing[F, T] = {
    val wrapped: F[T] = F.raiseError[T](error)
    delegate.thenReturn(wrapped)
  }

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing {
  implicit def toCatsFirstStubbing[F[_], T](v: OngoingStubbing[F[T]]): CatsStubbing[F, T] = CatsStubbing(v)

  implicit def toMock[F[_], T, M](s: CatsStubbing[F, T]): M = s.getMock[M]
}

trait MockitoCats {

  def whenF[F[_], T](methodCall: F[T]): CatsStubbing[F, T] = Mockito.when(methodCall)

}
