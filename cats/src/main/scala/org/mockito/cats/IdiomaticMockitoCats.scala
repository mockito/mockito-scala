package org.mockito.cats

import cats.{ Applicative, ApplicativeError, Eq }
import org.mockito._
import org.scalactic.Equality

import scala.reflect.ClassTag

trait IdiomaticMockitoCats extends ScalacticSerialisableHack {

  import org.mockito.cats.IdiomaticMockitoCats._

  implicit class StubbingOpsCats[F[_], T](stubbing: F[T]) {

    def shouldReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def mustReturnF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]
    def returnsF: ReturnActions[F, T] = macro WhenMacro.shouldReturn[T]

    def shouldFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def mustFailWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]
    def failsWith: ThrowActions[F, T] = macro WhenMacro.shouldThrow[T]

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

    def shouldAnswerFG: AnswerActions2[F, G, T] = macro WhenMacro.shouldAnswer[T]
    def mustAnswerFG: AnswerActions2[F, G, T] = macro WhenMacro.shouldAnswer[T]
    def answersFG: AnswerActions2[F, G, T] = macro WhenMacro.shouldAnswer[T]
  }

  val returnedF: ReturnedF.type   = ReturnedF
  val returnedFG: ReturnedFG.type = ReturnedFG
  val raised: Raised.type         = Raised
  val raisedG: RaisedG.type       = RaisedG
  implicit class DoSomethingOpsCats[R](v: R) {
    def willBe(r: ReturnedF.type): ReturnedByF[R]   = ReturnedByF[R]()
    def willBe(r: ReturnedFG.type): ReturnedByFG[R] = ReturnedByFG[R]()
    def willBe(r: Raised.type): Raised[R]           = Raised[R]()
    def willBe(r: RaisedG.type): RaisedG[R]         = RaisedG[R]()
  }

  implicit def catsEquality[T: Eq]: Equality[T] = new EqToEquality[T]
}

object IdiomaticMockitoCats extends IdiomaticMockitoCats {
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
    def by[F[_], E](stubbing: F[E])(implicit F: ApplicativeError[F, _ >: T]): F[E] = macro DoSomethingMacro.raised[E]
  }

  object RaisedG
  case class RaisedG[T]() {
    def by[F[_], G[_], E](stubbing: F[G[E]])(implicit F: Applicative[F], G: ApplicativeError[G, _ >: T]): F[G[E]] =
      macro DoSomethingMacro.raisedG[E]
  }

  class ReturnActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T] = os thenReturn value
  }

  class ReturnActions2[F[_], G[_], T](os: CatsStubbing2[F, G, T]) {
    def apply(value: T)(implicit a: Applicative[F], ag: Applicative[G]): CatsStubbing2[F, G, T] = os thenReturn value
  }

  class ThrowActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply[E](error: E)(implicit ae: ApplicativeError[F, _ >: E]): CatsStubbing[F, T] = os thenFailWith error
  }

  class ThrowActions2[F[_], G[_], T](os: CatsStubbing2[F, G, T]) {
    def apply[E](error: E)(implicit ae: Applicative[F], ag: ApplicativeError[G, _ >: E]): CatsStubbing2[F, G, T] = os thenFailWith error
  }

  class AnswerActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply(f: => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0: ClassTag](f: P0 => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1](f: (P0, P1) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2](f: (P0, P1, P2) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(
        implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(
        implicit F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T)(
        implicit F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f
  }

  class AnswerActions2[F[_], G[_], T](os: CatsStubbing2[F, G, T]) {
    def apply(f: => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0: ClassTag](f: P0 => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1](f: (P0, P1) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2](f: (P0, P1, P2) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T)(implicit F: Applicative[F],
                                                                        G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T)(implicit F: Applicative[F],
                                                                                G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F],
                                                                                        G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(implicit F: Applicative[F],
                                                                                                G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f
  }
}
