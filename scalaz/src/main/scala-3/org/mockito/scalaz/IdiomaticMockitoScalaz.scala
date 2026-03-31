package org.mockito.scalaz

import scalaz.{ Applicative, MonadError }
import org.mockito.*
import org.mockito.scalaz.IdiomaticMockitoScalazRuntime.*

/**
 * Low-priority willBe answered extensions for generic R values (non-function types). Function-specific extensions in IdiomaticMockitoScalaz take priority over these.
 */
private[mockito] trait LowPriorityScalazDoSomething {
  import org.mockito.scalaz.IdiomaticMockitoScalaz.*

  extension [R](inline v: R) {
    transparent inline def willBe(r: AnsweredF.type): AnsweredByF[R]   = AnsweredByF[R](() => v)
    transparent inline def willBe(r: AnsweredFG.type): AnsweredByFG[R] = AnsweredByFG[R](() => v)
  }
}

/**
 * Scala 3 version of IdiomaticMockitoScalaz with inline macro-based stubbing operations.
 */
private[mockito] trait IdiomaticMockitoScalaz extends IdiomaticMockitoScalazRuntime with LowPriorityScalazDoSomething {
  import org.mockito.scalaz.IdiomaticMockitoScalaz.*

  // ---- Stubbing extensions on F[T] ----

  extension [F[_], T](inline stubbing: F[T]) {
    transparent inline def shouldReturnF: ReturnActions[F, T] =
      new ReturnActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))
    transparent inline def mustReturnF: ReturnActions[F, T] =
      new ReturnActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))
    transparent inline def returnsF: ReturnActions[F, T] =
      new ReturnActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))

    transparent inline def shouldFailWith: ThrowActions[F, T] =
      new ThrowActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))
    transparent inline def mustFailWith: ThrowActions[F, T] =
      new ThrowActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))
    transparent inline def failsWith: ThrowActions[F, T] =
      new ThrowActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))
    transparent inline def raises: ThrowActions[F, T] =
      new ThrowActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))

    transparent inline def shouldAnswerF: AnswerActions[F, T] =
      new AnswerActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))
    transparent inline def mustAnswerF: AnswerActions[F, T] =
      new AnswerActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))
    transparent inline def answersF: AnswerActions[F, T] =
      new AnswerActions[F, T](ScalazStubbing(WhenMacro.whenRaw[F[T]](stubbing)))
  }

  // ---- Stubbing extensions on F[G[T]] ----

  extension [F[_], G[_], T](inline stubbing: F[G[T]]) {
    transparent inline def shouldReturnFG: ReturnActions2[F, G, T] =
      new ReturnActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))
    transparent inline def mustReturnFG: ReturnActions2[F, G, T] =
      new ReturnActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))
    transparent inline def returnsFG: ReturnActions2[F, G, T] =
      new ReturnActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))

    transparent inline def shouldFailWithG: ThrowActions2[F, G, T] =
      new ThrowActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))
    transparent inline def mustFailWithG: ThrowActions2[F, G, T] =
      new ThrowActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))
    transparent inline def failsWithG: ThrowActions2[F, G, T] =
      new ThrowActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))
    transparent inline def raisesG: ThrowActions2[F, G, T] =
      new ThrowActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))

    transparent inline def shouldAnswerFG: AnswerActions2[F, G, T] =
      new AnswerActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))
    transparent inline def mustAnswerFG: AnswerActions2[F, G, T] =
      new AnswerActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))
    transparent inline def answersFG: AnswerActions2[F, G, T] =
      new AnswerActions2[F, G, T](ScalazStubbing2(WhenMacro.whenRaw[F[G[T]]](stubbing)))
  }

  // ---- DoSomething extensions: willBe returnedF/returnedFG/raised/raisedG ----

  extension [R](v: R) {
    def willBe(r: ReturnedF.type): ReturnedByF[R]   = ReturnedByF[R](v)
    def willBe(r: ReturnedFG.type): ReturnedByFG[R] = ReturnedByFG[R](v)
    def willBe(r: Raised.type): RaisedBy[R]         = RaisedBy[R](v)
    def willBe(r: RaisedG.type): RaisedByG[R]       = RaisedByG[R](v)
  }

  // ---- DoSomething extensions: willBe answeredF/answeredFG ----
  // Function-specific extensions (higher priority than generic R in LowPriorityScalazDoSomething).
  // The macro inside `by` inspects T at compile time to determine function arity.

  extension [R](v: () => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[() => R]   = AnsweredByF[() => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[() => R] = AnsweredByFG[() => R](() => v)
  }

  extension [P0, R](v: P0 => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[P0 => R]   = AnsweredByF[P0 => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[P0 => R] = AnsweredByFG[P0 => R](() => v)
  }

  extension [P0, P1, R](v: (P0, P1) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1) => R]   = AnsweredByF[(P0, P1) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1) => R] = AnsweredByFG[(P0, P1) => R](() => v)
  }

  extension [P0, P1, P2, R](v: (P0, P1, P2) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1, P2) => R]   = AnsweredByF[(P0, P1, P2) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1, P2) => R] = AnsweredByFG[(P0, P1, P2) => R](() => v)
  }

  extension [P0, P1, P2, P3, R](v: (P0, P1, P2, P3) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1, P2, P3) => R]   = AnsweredByF[(P0, P1, P2, P3) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1, P2, P3) => R] = AnsweredByFG[(P0, P1, P2, P3) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, R](v: (P0, P1, P2, P3, P4) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1, P2, P3, P4) => R]   = AnsweredByF[(P0, P1, P2, P3, P4) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1, P2, P3, P4) => R] = AnsweredByFG[(P0, P1, P2, P3, P4) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, R](v: (P0, P1, P2, P3, P4, P5) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1, P2, P3, P4, P5) => R]   = AnsweredByF[(P0, P1, P2, P3, P4, P5) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1, P2, P3, P4, P5) => R] = AnsweredByFG[(P0, P1, P2, P3, P4, P5) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, P6, R](v: (P0, P1, P2, P3, P4, P5, P6) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1, P2, P3, P4, P5, P6) => R]   = AnsweredByF[(P0, P1, P2, P3, P4, P5, P6) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1, P2, P3, P4, P5, P6) => R] = AnsweredByFG[(P0, P1, P2, P3, P4, P5, P6) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, P6, P7, R](v: (P0, P1, P2, P3, P4, P5, P6, P7) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1, P2, P3, P4, P5, P6, P7) => R]   = AnsweredByF[(P0, P1, P2, P3, P4, P5, P6, P7) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1, P2, P3, P4, P5, P6, P7) => R] = AnsweredByFG[(P0, P1, P2, P3, P4, P5, P6, P7) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, P6, P7, P8, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1, P2, P3, P4, P5, P6, P7, P8) => R]   = AnsweredByF[(P0, P1, P2, P3, P4, P5, P6, P7, P8) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1, P2, P3, P4, P5, P6, P7, P8) => R] = AnsweredByFG[(P0, P1, P2, P3, P4, P5, P6, P7, P8) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R) {
    def willBe(a: AnsweredF.type): AnsweredByF[(P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R]   = AnsweredByF[(P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R](() => v)
    def willBe(a: AnsweredFG.type): AnsweredByFG[(P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R] = AnsweredByFG[(P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R](() => v)
  }
}

object IdiomaticMockitoScalaz extends IdiomaticMockitoScalaz {

  // ---- ReturnedByF / ReturnedByFG ----

  case class ReturnedByF[T](value: T) {
    transparent inline def by[F[_], S](inline stubbing: F[S])(using F: Applicative[F], ev: T <:< S): F[S] =
      DoSomethingMacro.doSomethingBy[F[S]](
        Mockito.doReturn(F.pure(ev(value))),
        stubbing
      )
  }

  case class ReturnedByFG[T](value: T) {
    transparent inline def by[F[_], G[_], S](inline stubbing: F[G[S]])(using F: Applicative[F], G: Applicative[G], ev: T <:< S): F[G[S]] =
      DoSomethingMacro.doSomethingBy[F[G[S]]](
        Mockito.doReturn(F.compose[G].pure(ev(value))),
        stubbing
      )
  }

  // ---- RaisedBy / RaisedByG ----

  case class RaisedBy[T](value: T) {
    transparent inline def by[F[_], E](inline stubbing: F[E])(using F: MonadError[F, ? >: T]): F[E] =
      DoSomethingMacro.doSomethingBy[F[E]](
        Mockito.doReturn(F.raiseError[E](value)),
        stubbing
      )
  }

  case class RaisedByG[T](value: T) {
    transparent inline def by[F[_], G[_], E](inline stubbing: F[G[E]])(using F: Applicative[F], G: MonadError[G, ? >: T]): F[G[E]] =
      DoSomethingMacro.doSomethingBy[F[G[E]]](
        Mockito.doReturn(F.pure(G.raiseError[E](value))),
        stubbing
      )
  }

  // ---- AnsweredByF / AnsweredByFG ----
  // Single generic wrapper per effect level. The macro inspects T at compile time
  // to determine if it's a function type (and its arity), building the appropriate answer.

  case class AnsweredByF[T](valueThunk: () => T) {
    transparent inline def by[F[_], S](inline stubbing: F[S])(using F: Applicative[F]): F[S] =
      DoSomethingMacro.answeredByWrappedThunkMacro[T, F[S]](
        valueThunk,
        stubbing,
        (a: Any) => F.pure(a.asInstanceOf[S])
      )
  }

  case class AnsweredByFG[T](valueThunk: () => T) {
    transparent inline def by[F[_], G[_], S](inline stubbing: F[G[S]])(using F: Applicative[F], G: Applicative[G]): F[G[S]] =
      DoSomethingMacro.answeredByWrappedThunkMacro[T, F[G[S]]](
        valueThunk,
        stubbing,
        (a: Any) => F.compose[G].pure(a.asInstanceOf[S])
      )
  }
}
