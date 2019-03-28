package org.mockito.matchers

import org.mockito.ArgumentMatchersSugar
import org.mockito.internal.ValueClassExtractor
import org.scalactic.Equality

trait DefaultMatcher[T] {
  def registerDefaultMatcher(value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T]): T
}

object DefaultMatcher {
  implicit def default[T]: DefaultMatcher[T] = new DefaultMatcher[T] {
    override def registerDefaultMatcher(value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T]): T =
      ArgumentMatchersSugar.eqTo(value)
  }

  def defaultMatcher[T](value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T], $def: DefaultMatcher[T]): T =
    $def.registerDefaultMatcher(value)
}
