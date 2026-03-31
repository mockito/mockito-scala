package org.mockito
package scalaz

import _root_.scalaz.Applicative
import org.mockito.internal.ValueClassWrapper
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

import scala.reflect.ClassTag

case class ScalazStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) extends ScalazStubbingBase[F, T] {
  // In Scala 3, String is implicitly convertible to WrappedString which extends Function1[Int, Char],
  // so a plain String value passed to thenAnswer resolves to the Function1 member overload (extensions
  // have lower priority). We detect this at runtime and treat the WrappedString as a constant value.
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F]): ScalazStubbing[F, T] =
    f match {
      case ws: scala.collection.immutable.WrappedString =>
        delegate thenAnswer invocationToAnswer(_ => ws.unwrap.asInstanceOf[T]).andThen(F.pure(_))
      case _ =>
        clazz[P0] match {
          case c if c == classOf[InvocationOnMock] => delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.pure(_))
          case _                                   => delegate thenAnswer functionToAnswer(f).andThen(F.pure(_))
        }
    }
}

object ScalazStubbing {
  implicit def toScalazStubbing[F[_], T](v: OngoingStubbing[F[T]]): ScalazStubbing[F, T] = ScalazStubbing(v)

  implicit def toMock[F[_], T, M](s: ScalazStubbing[F, T]): M = s.getMock[M]

  // by-name thenAnswer as extension (lower priority than member methods) to avoid Scala 3 overload ambiguity
  extension [F[_], T](cs: ScalazStubbing[F, T]) {
    def thenAnswer(f: => T)(implicit F: Applicative[F]): ScalazStubbing[F, T] =
      cs.delegate thenAnswer invocationToAnswer(_ => f).andThen(F.pure(_))
  }
}

case class ScalazStubbing2[F[_], G[_], T](delegate: OngoingStubbing[F[G[T]]]) extends ScalazStubbing2Base[F, G, T] {
  // WrappedString guard (same as ScalazStubbing above)
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
    f match {
      case ws: scala.collection.immutable.WrappedString =>
        delegate thenAnswer invocationToAnswer(_ => ws.unwrap.asInstanceOf[T]).andThen(F.compose[G].pure(_))
      case _ =>
        clazz[P0] match {
          case c if c == classOf[InvocationOnMock] =>
            delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.compose[G].pure(_))
          case _ =>
            delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure(_))
        }
    }
}

object ScalazStubbing2 {
  implicit def toScalazStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): ScalazStubbing2[F, G, T] = ScalazStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: ScalazStubbing2[F, G, T]): M = s.getMock[M]

  // by-name thenAnswer as extension (lower priority than member methods) to avoid Scala 3 overload ambiguity
  extension [F[_], G[_], T](cs: ScalazStubbing2[F, G, T]) {
    def thenAnswer(f: => T)(implicit F: Applicative[F], G: Applicative[G]): ScalazStubbing2[F, G, T] =
      cs.delegate thenAnswer invocationToAnswer(_ => f).andThen(F.compose[G].pure(_))
  }
}
