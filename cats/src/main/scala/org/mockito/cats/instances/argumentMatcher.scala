package org.mockito.cats

import cats._
import org.mockito.ArgumentMatcher
import org.mockito.matchers._

trait ArgumentMatcherInstances {
  implicit val argumentMatcherInstance: ContravariantMonoidal[ArgumentMatcher] with MonoidK[ArgumentMatcher] =
    new ContravariantMonoidal[ArgumentMatcher] with MonoidK[ArgumentMatcher] {
      override def unit                                                          = narrow(AnyArg)
      override def empty[A]                                                      = narrow(AnyArg)
      override def contramap[A, B](fa: ArgumentMatcher[A])(f: B => A)            = Transformed(fa)(f)
      override def product[A, B](fa: ArgumentMatcher[A], fb: ArgumentMatcher[B]) = ProductOf(fa, fb)
      override def combineK[A](x: ArgumentMatcher[A], y: ArgumentMatcher[A])     = AllOf(x, y)
    }
}

object ArgumentMatcherInstances extends ArgumentMatcherInstances
