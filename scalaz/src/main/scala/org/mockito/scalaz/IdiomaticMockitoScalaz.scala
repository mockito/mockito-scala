package org.mockito.scalaz

import scalaz.{ Applicative, Equal, MonadError }
import org.mockito._
import org.scalactic.Equality

trait IdiomaticMockitoScalaz extends ScalacticSerialisableHack {

  import org.mockito.scalaz.IdiomaticMockitoScalaz._

  implicit class StubbingOpsScalaz[F[_], T](stubbing: F[T]) {

    def shouldReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def mustReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def returnsF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]

    def shouldFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def mustFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def failsWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def raises: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
  }

  implicit class StubbingOps2Scalaz[F[_], G[_], T](stubbing: F[G[T]]) {

    def shouldReturnFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]
    def mustReturnFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]
    def returnsFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]

    def shouldFailWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
    def mustFailWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
    def failsWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
    def raisesG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
  }

  val returnedF: ReturnedF.type   = ReturnedF
  val returnedFG: ReturnedFG.type = ReturnedFG
  val raised: Raised.type         = Raised
  val raisedG: RaisedG.type       = RaisedG
  implicit class DoSomethingOpsScalaz[R](v: R) {
    def willBe(r: ReturnedF.type): ReturnedByF[R]   = ReturnedByF[R]()
    def willBe(r: ReturnedFG.type): ReturnedByFG[R] = ReturnedByFG[R]()
    def willBe(r: Raised.type): Raised[R]           = Raised[R]()
    def willBe(r: RaisedG.type): RaisedG[R]         = RaisedG[R]()
  }

  implicit def scalazEquality[T: Equal]: Equality[T] = new EqToEquality[T]
}

object IdiomaticMockitoScalaz extends IdiomaticMockitoScalaz {
  object ReturnedF
  case class ReturnedByF[T]() {
    def by[F[_], S](stubbing: F[S])(implicit F: Applicative[F], $ev: T <:< S): F[S] = macro DoSomethingMacro.returnedF[T, S]
  }

  object ReturnedFG
  case class ReturnedByFG[T]() {
    def by[F[_], G[_], S](stubbing: F[G[S]])(implicit F: Applicative[F], G: Applicative[G], $ev: T <:< S): F[G[S]] =
      macro DoSomethingMacro.returnedFG[T, S]
  }

  object Raised
  case class Raised[T]() {
    def by[F[_], E](stubbing: F[E])(implicit F: MonadError[F, _ >: T]): F[E] = macro DoSomethingMacro.raised[E]
  }

  object RaisedG
  case class RaisedG[T]() {
    def by[F[_], G[_], E](stubbing: F[G[E]])(implicit F: Applicative[F], G: MonadError[G, _ >: T]): F[G[E]] =
      macro DoSomethingMacro.raisedG[E]
  }

  class ReturnActions[F[_], T](os: ScalazStubbing[F, T]) {
    def apply(value: T)(implicit a: Applicative[F]): ScalazStubbing[F, T] = os thenReturn value
  }

  class ReturnActions2[F[_], G[_], T](os: ScalazStubbing2[F, G, T]) {
    def apply(value: T)(implicit a: Applicative[F], ag: Applicative[G]): ScalazStubbing2[F, G, T] = os thenReturn value
  }

  class ThrowActions[F[_], T](os: ScalazStubbing[F, T]) {
    def apply[E](error: E)(implicit ae: MonadError[F, _ >: E]): ScalazStubbing[F, T] = os thenFailWith error
  }

  class ThrowActions2[F[_], G[_], T](os: ScalazStubbing2[F, G, T]) {
    def apply[E](error: E)(implicit ae: Applicative[F], ag: MonadError[G, _ >: E]): ScalazStubbing2[F, G, T] = os thenFailWith error
  }
}
