package org.mockito
package scalaz

import _root_.scalaz.Applicative
import org.mockito.internal.ValueClassWrapper
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

import scala.reflect.ClassTag

/**
 * Scala 2 stubbing facade for scalaz effect-returning methods.
 *
 * Common logic (thenReturn/andThen and Function2..Function22 overloads) lives in [[ScalazStubbingBase]]. This Scala 2 implementation keeps the by-name and Function1 `thenAnswer`
 * overloads as members because Scala 2 and Scala 3 resolve this pair differently, and Scala 3 therefore uses a different placement/priority strategy.
 */
case class ScalazStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) extends ScalazStubbingBase[F, T] {
  def thenAnswer(f: => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] =
    delegate thenAnswer invocationToAnswer(_ => f).andThen(F.pure(_))
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F]): ScalazStubbing[F, T] =
    clazz[P0] match {
      case c if c == classOf[InvocationOnMock] => delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.pure(_))
      case _                                   => delegate thenAnswer functionToAnswer(f).andThen(F.pure(_))
    }
}

/** Scala 2 implicit conversions for scalaz stubbing DSL. */
object ScalazStubbing {
  implicit def toScalazStubbing[F[_], T](v: OngoingStubbing[F[T]]): ScalazStubbing[F, T] = ScalazStubbing(v)

  implicit def toMock[F[_], T, M](s: ScalazStubbing[F, T]): M = s.getMock[M]
}

/**
 * Scala 2 stubbing facade for nested scalaz effects `F[G[T]]`.
 *
 * Same rationale as [[ScalazStubbing]] regarding by-name and Function1 overloads.
 */
case class ScalazStubbing2[F[_], G[_], T](delegate: OngoingStubbing[F[G[T]]]) extends ScalazStubbing2Base[F, G, T] {
  def thenAnswer(f: => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
    delegate thenAnswer invocationToAnswer(_ => f).andThen(F.compose[G].pure(_))
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
    clazz[P0] match {
      case c if c == classOf[InvocationOnMock] =>
        delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.compose[G].pure(_))
      case _ =>
        delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure(_))
    }
}

/** Scala 2 implicit conversions for nested scalaz stubbing DSL. */
object ScalazStubbing2 {
  implicit def toScalazStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): ScalazStubbing2[F, G, T] = ScalazStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: ScalazStubbing2[F, G, T]): M = s.getMock[M]
}
