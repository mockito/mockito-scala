package org.mockito.cats

import cats.{ Applicative, ApplicativeError, Eq }
import org.mockito.*
import org.scalactic.Equality

import scala.reflect.ClassTag

/**
 * Runtime support for IdiomaticMockitoCats. Shared across Scala 2 and Scala 3. Macro-based operations are in version-specific IdiomaticMockitoCats.
 */
object IdiomaticMockitoCatsRuntime {
  object ReturnedF
  object AnsweredF
  object ReturnedFG
  object AnsweredFG
  object Raised
  object RaisedG

  class ReturnActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply(value: T)(implicit a: Applicative[F]): CatsStubbing[F, T] = os thenReturn value
  }

  class ReturnActions2[F[_], G[_], T](os: CatsStubbing2[F, G, T]) {
    def apply(value: T)(implicit a: Applicative[F], ag: Applicative[G]): CatsStubbing2[F, G, T] = os thenReturn value
  }

  class ThrowActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply[E](error: E)(implicit ae: ApplicativeError[F, ? >: E]): CatsStubbing[F, T] = os thenFailWith error
  }

  class ThrowActions2[F[_], G[_], T](os: CatsStubbing2[F, G, T]) {
    def apply[E](error: E)(implicit ae: Applicative[F], ag: ApplicativeError[G, ? >: E]): CatsStubbing2[F, G, T] = os thenFailWith error
  }

  class AnswerActions[F[_], T](os: CatsStubbing[F, T]) {
    def apply(f: => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1](f: (P0, P1) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2](f: (P0, P1, P2) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
      os thenAnswer f
  }

  class AnswerActions2[F[_], G[_], T](os: CatsStubbing2[F, G, T]) {
    def apply(f: => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1](f: (P0, P1) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2](f: (P0, P1, P2) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T
    )(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      os thenAnswer f
  }
}

trait IdiomaticMockitoCatsRuntime extends ScalacticSerialisableHack {
  import org.mockito.cats.IdiomaticMockitoCatsRuntime.*

  val returnedF: ReturnedF.type   = ReturnedF
  val answeredF: AnsweredF.type   = AnsweredF
  val returnedFG: ReturnedFG.type = ReturnedFG
  val answeredFG: AnsweredFG.type = AnsweredFG
  val raised: Raised.type         = Raised
  val raisedG: RaisedG.type       = RaisedG

  implicit def catsEquality[T: Eq]: Equality[T] = new EqToEquality[T]
}
