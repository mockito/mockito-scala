package org.mockito.matchers

import org.mockito.ArgumentMatchersSugar
import org.mockito.internal.ValueClassExtractor
import org.scalactic.{ Equality, Prettifier }

trait DefaultMatcher[T] {
  def registerDefaultMatcher(value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T], $pt: Prettifier): T
}

object DefaultMatcher {
  implicit def default[T]: DefaultMatcher[T] = new DefaultMatcher[T] {
    override def registerDefaultMatcher(value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T], $pt: Prettifier): T =
      ArgumentMatchersSugar.eqTo(value)
  }

  def defaultMatcher[T: Equality: ValueClassExtractor](value: T)(implicit $def: DefaultMatcher[T], $pt: Prettifier): T =
    $def.registerDefaultMatcher(value)
}
