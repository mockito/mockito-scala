package org.mockito.matchers

import org.mockito.ArgumentMatcher
import org.mockito.internal.ValueClassExtractor
import org.scalactic.TripleEquals._
import org.scalactic.{Equality, Prettifier}

case class EqTo[T](value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T], $pt: Prettifier) extends ArgumentMatcher[T] {
  override def matches(argument: T): Boolean = value === argument || $vce.extractAs[T](value) === argument
  override def toString: String = $pt(value)
}
