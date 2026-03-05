package org.mockito.scalaz

import scalaz.{ Applicative, Equal, MonadError }
import org.mockito.*
import org.scalactic.Equality

import scala.reflect.ClassTag

/**
 * Runtime support for IdiomaticMockitoScalaz. Shared across Scala 2 and Scala 3. Macro-based operations are in version-specific IdiomaticMockitoScalaz.
 */
object IdiomaticMockitoScalazRuntime {
  object ReturnedF
  object AnsweredF
  object ReturnedFG
  object AnsweredFG
  object Raised
  object RaisedG

  class ReturnActions[F[_], T](os: ScalazStubbing[F, T]) {
    def apply(value: T)(implicit a: Applicative[F]): ScalazStubbing[F, T] = os thenReturn value
  }

  class ReturnActions2[F[_], G[_], T](os: ScalazStubbing2[F, G, T]) {
    def apply(value: T)(implicit a: Applicative[F], ag: Applicative[G]): ScalazStubbing2[F, G, T] = os thenReturn value
  }

  class ThrowActions[F[_], T](os: ScalazStubbing[F, T]) {
    def apply[E](error: E)(implicit ae: MonadError[F, ? >: E]): ScalazStubbing[F, T] = os thenFailWith error
  }

  class ThrowActions2[F[_], G[_], T](os: ScalazStubbing2[F, G, T]) {
    def apply[E](error: E)(implicit ae: Applicative[F], ag: MonadError[G, ? >: E]): ScalazStubbing2[F, G, T] = os thenFailWith error
  }

  class AnswerActions[F[_], T](os: ScalazStubbing[F, T]) {
    def apply(f: => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] = os thenAnswer f

    def apply[P0](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F]): ScalazStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1](f: (P0, P1) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2](f: (P0, P1, P2) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] =
      os thenAnswer f
  }

  class AnswerActions2[F[_], G[_], T](os: ScalazStubbing2[F, G, T]) {
    def apply(f: => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] = os thenAnswer f

    def apply[P0](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1](f: (P0, P1) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2](f: (P0, P1, P2) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](
        f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T
    )(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      os thenAnswer f
  }
}

trait IdiomaticMockitoScalazRuntime extends ScalacticSerialisableHack {
  import org.mockito.scalaz.IdiomaticMockitoScalazRuntime.*

  val returnedF: ReturnedF.type   = ReturnedF
  val answeredF: AnsweredF.type   = AnsweredF
  val returnedFG: ReturnedFG.type = ReturnedFG
  val answeredFG: AnsweredFG.type = AnsweredFG
  val raised: Raised.type         = Raised
  val raisedG: RaisedG.type       = RaisedG

  implicit def scalazEquality[T: Equal]: Equality[T] = new EqToEquality[T]
}
