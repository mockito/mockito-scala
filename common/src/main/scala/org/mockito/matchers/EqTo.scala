package org.mockito.matchers

import org.mockito.ArgumentMatcher
import org.mockito.internal.ValueClassExtractor
import org.scalactic.TripleEquals._
import org.scalactic.{ Equality, Prettifier }

case class EqTo[T: Equality: ValueClassExtractor](value: T)(implicit $pt: Prettifier) extends ArgumentMatcher[T] {
  private lazy val rawValue: T = ValueClassExtractor[T].extractAs[T](value)

  override def matches(argument: T): Boolean = value === argument || rawValue === argument

  override def toString: String = $pt(value)
}
