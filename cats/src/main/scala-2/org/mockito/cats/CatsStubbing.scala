package org.mockito
package cats

import _root_.cats.Applicative
import org.mockito.internal.ValueClassWrapper
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

import scala.reflect.ClassTag

/**
 * Scala 2 stubbing facade for cats effect-returning methods.
 *
 * Common logic (thenReturn/andThen and Function2..Function22 overloads) lives in [[CatsStubbingBase]]. This Scala 2 implementation keeps the by-name and Function1 `thenAnswer`
 * overloads as members because Scala 2 and Scala 3 resolve this pair differently, and Scala 3 therefore uses a different placement/priority strategy.
 */
case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) extends CatsStubbingBase[F, T] {
  def thenAnswer(f: => T)(implicit F: Applicative[F]): CatsStubbing[F, T] = delegate thenAnswer invocationToAnswer(_ => f).andThen(F.pure)
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F]): CatsStubbing[F, T] =
    clazz[P0] match {
      case c if c == classOf[InvocationOnMock] => delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.pure)
      case _                                   => delegate thenAnswer functionToAnswer(f).andThen(F.pure)
    }
}

/** Scala 2 implicit conversions for cats stubbing DSL. */
object CatsStubbing {
  implicit def toCatsStubbing[F[_], T](v: OngoingStubbing[F[T]]): CatsStubbing[F, T] = CatsStubbing(v)

  implicit def toMock[F[_], T, M](s: CatsStubbing[F, T]): M = s.getMock[M]
}

/**
 * Scala 2 stubbing facade for nested cats effects `F[G[T]]`.
 *
 * Same rationale as [[CatsStubbing]] regarding by-name and Function1 overloads.
 */
case class CatsStubbing2[F[_], G[_], T](delegate: OngoingStubbing[F[G[T]]]) extends CatsStubbing2Base[F, G, T] {
  def thenAnswer(f: => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    delegate thenAnswer invocationToAnswer(_ => f).andThen(F.compose[G].pure)
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    clazz[P0] match {
      case c if c == classOf[InvocationOnMock] =>
        delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.compose[G].pure)
      case _ =>
        delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
    }
}

/** Scala 2 implicit conversions for nested cats stubbing DSL. */
object CatsStubbing2 {
  implicit def toCatsStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): CatsStubbing2[F, G, T] = CatsStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: CatsStubbing2[F, G, T]): M = s.getMock[M]
}
