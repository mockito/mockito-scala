package org.mockito.cats

import cats._
import org.mockito.ArgumentMatcher
import org.mockito.internal.matchers._

case class MappedArgumentMatcher[A, B](fa: ArgumentMatcher[A], f: B => A) extends ArgumentMatcher[B] {
  override def matches(b: B) = fa.matches(f(b))
}

case class ProductArgumentMatcher[A, B](fa: ArgumentMatcher[A], fb: ArgumentMatcher[B]) extends ArgumentMatcher[(A, B)] {
  override def matches(ab: (A, B)) = fa.matches(ab._1) && fb.matches(ab._2)
}

trait ArgumentMatcherInstances {
  implicit val argumentMatcherInstance: ContravariantMonoidal[ArgumentMatcher] with MonoidK[ArgumentMatcher] =
    new ContravariantMonoidal[ArgumentMatcher] with MonoidK[ArgumentMatcher] {
      override def unit                                                          = narrow[scala.Any, Unit](Any.ANY)
      override def empty[A]                                                      = narrow[scala.Any, A](Any.ANY)
      override def contramap[A, B](fa: ArgumentMatcher[A])(f: B => A)            = MappedArgumentMatcher(fa, f)
      override def product[A, B](fa: ArgumentMatcher[A], fb: ArgumentMatcher[B]) = ProductArgumentMatcher(fa, fb)
      override def combineK[A](x: ArgumentMatcher[A], y: ArgumentMatcher[A])     = new And(x, y).asInstanceOf[ArgumentMatcher[A]]
    }
}

object ArgumentMatcherInstances extends ArgumentMatcherInstances
