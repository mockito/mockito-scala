package org.mockito.cats

import cats.{Applicative, ApplicativeError, Eq}
import org.mockito._
import org.mockito.cats.IdiomaticMockitoCats.{ReturnActions, ReturnActions2, ThrowActions, ThrowActions2}
import org.scalactic.Equality

trait IdiomaticMockitoCats extends ScalacticSerialisableHack {

  implicit class StubbingOps[F[_], T](stubbing: F[T]) {

    def shouldReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def mustReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def returnsF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]

    def shouldFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def mustFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def failsWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
  }

  implicit class StubbingOps2[F[_], G[_], T](stubbing: F[G[T]]) {

    def shouldReturnFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]
    def mustReturnFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]
    def returnsFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]

    def shouldFailWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
    def mustFailWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
    def failsWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
  }

  implicit def catsEquality[T: Eq]: Equality[T] = new EqToEquality[T]
}

object IdiomaticMockitoCats extends IdiomaticMockitoCats {

  class ReturnActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T] = os thenReturn value
  }

  class ReturnActions2[F[_], G[_], T](os: CatsStubbing2[F, G, T]) {
    def apply(value: T)(implicit a: Applicative[F], ag: Applicative[G]): CatsStubbing2[F, G, T] = os thenReturn value
  }

  class ThrowActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply[E](error: E)(implicit ae: ApplicativeError[F, E]): CatsStubbing[F, T] = os thenFailWith error
  }

  class ThrowActions2[F[_], G[_], T](os: CatsStubbing2[F, G, T]) {
    def apply[E](error: E)(implicit ae: Applicative[F], ag: ApplicativeError[G, E]): CatsStubbing2[F, G, T] = os thenFailWith error
  }
}
