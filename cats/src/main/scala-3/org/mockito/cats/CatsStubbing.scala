package org.mockito
package cats

import _root_.cats.Applicative
import org.mockito.internal.ValueClassWrapper
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.OngoingStubbing

import scala.reflect.ClassTag

case class CatsStubbing[F[_], T](delegate: OngoingStubbing[F[T]]) extends CatsStubbingBase[F, T] {
  // In Scala 3, String is implicitly convertible to WrappedString which extends Function1[Int, Char],
  // so a plain String value passed to thenAnswer resolves to the Function1 member overload (extensions
  // have lower priority). We detect this at runtime and treat the WrappedString as a constant value.
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F]): CatsStubbing[F, T] =
    f match {
      case ws: scala.collection.immutable.WrappedString =>
        delegate thenAnswer invocationToAnswer(_ => ws.unwrap.asInstanceOf[T]).andThen(F.pure)
      case _ =>
        clazz[P0] match {
          case c if c == classOf[InvocationOnMock] => delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.pure)
          case _                                   => delegate thenAnswer functionToAnswer(f).andThen(F.pure)
        }
    }
}

object CatsStubbing {
  implicit def toCatsStubbing[F[_], T](v: OngoingStubbing[F[T]]): CatsStubbing[F, T] = CatsStubbing(v)

  implicit def toMock[F[_], T, M](s: CatsStubbing[F, T]): M = s.getMock[M]

  // by-name thenAnswer as extension (lower priority than member methods) to avoid Scala 3 overload ambiguity
  extension [F[_], T](cs: CatsStubbing[F, T]) {
    def thenAnswer(f: => T)(implicit F: Applicative[F]): CatsStubbing[F, T] =
      cs.delegate thenAnswer invocationToAnswer(_ => f).andThen(F.pure)
  }
}

case class CatsStubbing2[F[_], G[_], T](delegate: OngoingStubbing[F[G[T]]]) extends CatsStubbing2Base[F, G, T] {
  // WrappedString guard (same as CatsStubbing above)
  def thenAnswer[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0], F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
    f match {
      case ws: scala.collection.immutable.WrappedString =>
        delegate thenAnswer invocationToAnswer(_ => ws.unwrap.asInstanceOf[T]).andThen(F.compose[G].pure)
      case _ =>
        clazz[P0] match {
          case c if c == classOf[InvocationOnMock] =>
            delegate thenAnswer invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(F.compose[G].pure)
          case _ =>
            delegate thenAnswer functionToAnswer(f).andThen(F.compose[G].pure)
        }
    }
}

object CatsStubbing2 {
  implicit def toCatsStubbing[F[_], G[_], T](v: OngoingStubbing[F[G[T]]]): CatsStubbing2[F, G, T] = CatsStubbing2(v)

  implicit def toMock[F[_], G[_], T, M](s: CatsStubbing2[F, G, T]): M = s.getMock[M]

  // by-name thenAnswer as extension (lower priority than member methods) to avoid Scala 3 overload ambiguity
  extension [F[_], G[_], T](cs: CatsStubbing2[F, G, T]) {
    def thenAnswer(f: => T)(implicit F: Applicative[F], G: Applicative[G]): CatsStubbing2[F, G, T] =
      cs.delegate thenAnswer invocationToAnswer(_ => f).andThen(F.compose[G].pure)
  }
}
