package org.mockito.scalaz

import org.mockito.stubbing.OngoingStubbing
import scalaz.{ Applicative, ApplicativeError }

case class ScalazStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) {

  def thenReturn(value: T)(implicit a: Applicative[F]): ScalazStubbing[F, T] = delegate thenReturn a.pure(value)
  def andThen(value: T)(implicit a: Applicative[F]): ScalazStubbing[F, T]    = thenReturn(value)
  def andThen(value: F[T]): ScalazStubbing[F, T]                             = delegate thenReturn value

  def thenFailWith[E](error: E)(implicit ae: ApplicativeError[F, _ >: E]): ScalazStubbing[F, T] =
    delegate thenReturn ae.raiseError[T](error)

  def getMock[M]: M = delegate.getMock[M]
}

object ScalazStubbing {
  implicit def toScalazStubbing[F[_], T](v: OngoingStubbing[F[T]]): ScalazStubbing[F, T] = ScalazStubbing(v)

  implicit def toMock[F[_], T, M](s: ScalazStubbing[F, T]): M = s.getMock[M]
}

case class ScalazStubbing2[F[_], G[_], T](delegate: OngoingStubbing[F[G[T]]]) {

  def thenReturn(value: T)(implicit af: Applicative[F], ag: Applicative[G]): ScalazStubbing2[F, G, T] =
    delegate thenReturn af.compose[G].pure(value)
  def andThen(value: T)(implicit af: Applicative[F], ag: Applicative[G]): ScalazStubbing2[F, G, T] = thenReturn(value)
  def andThen(value: G[T])(implicit af: Applicative[F]): ScalazStubbing2[F, G, T]                  = delegate thenReturn af.pure(value)
  def andThen(value: F[G[T]]): ScalazStubbing2[F, G, T]                                            = delegate thenReturn value

  def thenFailWith[E](error: E)(implicit ae: Applicative[F], ag: ApplicativeError[G, _ >: E]): ScalazStubbing2[F, G, T] =
    delegate thenReturn ae.pure(ag.raiseError[T](error))

  def getMock[M]: M = delegate.getMock[M]
}

object ScalazStubbing2 {
  implicit def toScalazStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): ScalazStubbing2[F, G, T] = ScalazStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: ScalazStubbing2[F, G, T]): M = s.getMock[M]
}
