package org.mockito.cats

import cats.{ Applicative, ApplicativeError, Eq }
import org.mockito._
import org.mockito.cats.IdiomaticMockitoCats.{ ReturnActions, ThrowActions }
import org.scalactic.Equality

import scala.reflect.ClassTag

trait IdiomaticMockitoCats extends IdiomaticMockito {

  implicit class StubbingOps[F[_], T](stubbing: F[T]) {

    def shouldReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def mustReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def returnsF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]

    def shouldFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def mustFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def failsWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
  }

  def whenF[F[_], T](methodCall: F[T]): CatsStubbing[F, T] = Mockito.when(methodCall)

  implicit def catsEquality[T: ClassTag: Eq]: Equality[T] = new EqToEquality[T]
}

object IdiomaticMockitoCats extends IdiomaticMockitoCats {

  class ReturnActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T] = os thenReturn value
  }

  class ThrowActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply[E](error: E)(implicit ae: ApplicativeError[F, E]): CatsStubbing[F, T] = os thenFailWith error
  }
}
