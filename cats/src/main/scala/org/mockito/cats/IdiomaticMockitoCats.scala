package org.mockito.cats

import cats.{ Applicative, ApplicativeError }
import org.mockito.*

/**
 * Scala 2 version of IdiomaticMockitoCats with macro-based stubbing operations.
 */
trait IdiomaticMockitoCats extends IdiomaticMockitoCatsRuntime {
  import org.mockito.cats.IdiomaticMockitoCats.*
  import org.mockito.cats.IdiomaticMockitoCatsRuntime.{
    AnswerActions,
    AnswerActions2,
    AnsweredF,
    AnsweredFG,
    Raised,
    RaisedG,
    ReturnActions,
    ReturnActions2,
    ReturnedF,
    ReturnedFG,
    ThrowActions,
    ThrowActions2
  }

  implicit class StubbingOpsCats[F[_], T](stubbing: F[T]) {
    def shouldReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def mustReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def returnsF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]

    def shouldFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def mustFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def failsWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def raises: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]

    def shouldAnswerF: AnswerActions[F, T] = macro WhenMacro.shouldAnswer[T]
    def mustAnswerF: AnswerActions[F, T] = macro WhenMacro.shouldAnswer[T]
    def answersF: AnswerActions[F, T] = macro WhenMacro.shouldAnswer[T]
  }

  implicit class StubbingOps2Cats[F[_], G[_], T](stubbing: F[G[T]]) {
    def shouldReturnFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]
    def mustReturnFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]
    def returnsFG: ReturnActions2[F, G, T] = macro WhenMacro.shouldReturn[T]

    def shouldFailWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
    def mustFailWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
    def failsWithG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]
    def raisesG: ThrowActions2[F, G, T] = macro WhenMacro.shouldThrow[T]

    def shouldAnswerFG: AnswerActions2[F, G, T] = macro WhenMacro.shouldAnswer[T]
    def mustAnswerFG: AnswerActions2[F, G, T] = macro WhenMacro.shouldAnswer[T]
    def answersFG: AnswerActions2[F, G, T] = macro WhenMacro.shouldAnswer[T]
  }

  implicit class DoSomethingOpsCats[R](v: R) {
    def willBe(r: ReturnedF.type): ReturnedByF[R]   = ReturnedByF[R]()
    def willBe(r: ReturnedFG.type): ReturnedByFG[R] = ReturnedByFG[R]()
    def willBe(r: Raised.type): RaisedBy[R]         = RaisedBy[R]()
    def willBe(r: RaisedG.type): RaisedByG[R]       = RaisedByG[R]()
    def willBe(r: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(r: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps0Cats[R](v: () => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps1Cats[P0, R](v: P0 => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps2Cats[P0, P1, R](v: (P0, P1) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps3Cats[P0, P1, P2, R](v: (P0, P1, P2) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps4Cats[P0, P1, P2, P3, R](v: (P0, P1, P2, P3) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps5Cats[P0, P1, P2, P3, P4, R](v: (P0, P1, P2, P3, P4) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps6Cats[P0, P1, P2, P3, P4, P5, R](v: (P0, P1, P2, P3, P4, P5) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps7Cats[P0, P1, P2, P3, P4, P5, P6, R](v: (P0, P1, P2, P3, P4, P5, P6) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps8Cats[P0, P1, P2, P3, P4, P5, P6, P7, R](v: (P0, P1, P2, P3, P4, P5, P6, P7) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps9Cats[P0, P1, P2, P3, P4, P5, P6, P7, P8, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }

  implicit class DoSomethingOps10Cats[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R]()
    def willBe(a: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R]()
  }
}

object IdiomaticMockitoCats extends IdiomaticMockitoCats {

  // Re-export runtime action classes for macro-generated code
  type ReturnActions[F[_], T]        = IdiomaticMockitoCatsRuntime.ReturnActions[F, T]
  type ReturnActions2[F[_], G[_], T] = IdiomaticMockitoCatsRuntime.ReturnActions2[F, G, T]
  type ThrowActions[F[_], T]         = IdiomaticMockitoCatsRuntime.ThrowActions[F, T]
  type ThrowActions2[F[_], G[_], T]  = IdiomaticMockitoCatsRuntime.ThrowActions2[F, G, T]
  type AnswerActions[F[_], T]        = IdiomaticMockitoCatsRuntime.AnswerActions[F, T]
  type AnswerActions2[F[_], G[_], T] = IdiomaticMockitoCatsRuntime.AnswerActions2[F, G, T]

  case class ReturnedByF[T]() {
    def by[F[_], S](stubbing: F[S])(implicit F: Applicative[F], $ev: T <:< S): F[S] = macro DoSomethingMacro.returnedF[T, S]
  }

  case class AnsweredByF[T]() {
    def by[F[_], S](stubbing: F[S])(implicit F: Applicative[F], $ev: T <:< S): F[S] = macro DoSomethingMacro.answeredF[T, S]
  }

  case class ReturnedByFG[T]() {
    def by[F[_], G[_], S](stubbing: F[G[S]])(implicit F: Applicative[F], G: Applicative[G], $ev: T <:< S): F[G[S]] =
      macro DoSomethingMacro.returnedFG[T, S]
  }

  case class AnsweredByFG[T]() {
    def by[F[_], G[_], S](stubbing: F[G[S]])(implicit F: Applicative[F], G: Applicative[G], $ev: T <:< S): F[G[S]] =
      macro DoSomethingMacro.answeredFG[T, S]
  }

  case class RaisedBy[T]() {
    def by[F[_], E](stubbing: F[E])(implicit F: ApplicativeError[F, ? >: T]): F[E] = macro DoSomethingMacro.raised[E]
  }

  case class RaisedByG[T]() {
    def by[F[_], G[_], E](stubbing: F[G[E]])(implicit F: Applicative[F], G: ApplicativeError[G, ? >: T]): F[G[E]] =
      macro DoSomethingMacro.raisedG[E]
  }
}
