package org.mockito.matchers

import org.mockito.ArgumentMatchersSugar
import org.mockito.internal.ValueClassExtractor
import org.scalactic.{ Equality, Prettifier }

trait DefaultMatcher[T] {
  def registerDefaultMatcher(value: T): T
}

object DefaultMatcher {
  implicit def default[T: Equality: ValueClassExtractor](implicit prettifier: Prettifier): DefaultMatcher[T] = new DefaultMatcher[T] {
    override def registerDefaultMatcher(value: T): T = ArgumentMatchersSugar.eqTo(value)
  }

  def apply[T: Equality: ValueClassExtractor: DefaultMatcher](value: T): T =
    implicitly[DefaultMatcher[T]].registerDefaultMatcher(value)
}
