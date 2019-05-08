package org.mockito.cats

import cats.{Applicative, ApplicativeError, Eq}
import cats.implicits._
import org.mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalactic.Equality

import scala.reflect.ClassTag

case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) {

  def thenReturn(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T] = delegate.thenReturn(value.pure[F])

  def thenFailWith[E](error: E)(implicit ae: ApplicativeError[F, E]): CatsStubbing[F, T] = delegate.thenReturn(error.raiseError[F, T])

  def getMock[M]: M = delegate.getMock[M]
}

object CatsStubbing {
  implicit def toCatsFirstStubbing[F[_], T](v: OngoingStubbing[F[T]]): CatsStubbing[F, T] = CatsStubbing(v)
}

class EqToEquality[T: ClassTag: Eq] extends Equality[T] {
  override def areEqual(a: T, b: Any): Boolean = clazz[T].isInstance(b) && a === b.asInstanceOf[T]
}

trait MockitoCats extends MockitoSugar {

  def whenF[F[_], T](methodCall: F[T]): CatsStubbing[F, T] = Mockito.when(methodCall)

  implicit def catsEquality[T: ClassTag: Eq]: Equality[T] = new EqToEquality[T]
}
