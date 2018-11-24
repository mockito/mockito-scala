package org.mockito.matchers

import org.mockito.ArgumentMatcher
import org.mockito.internal.ValueClassExtractor
import org.scalactic.Equality

trait EqMatchers_213 {

  /**
    * Creates a matcher that delegates on {{org.scalactic.Equality}} so you can always customise how the values are compared
    * Also works with value classes
    */
  def eqTo[T](value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T]): T = {
    val extractedValue = $vce.extract(value)
    ThatMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = $eq.areEqual(extractedValue.asInstanceOf[T], v)
      override def toString: String       = s"eqTo($value)"
    })
    value
  }

  /**
    * It was intended to be used instead of eqTo when the argument is a value class,
    * but eqTo now supports value classes so it is not needed anymore
    */
  @deprecated("Use 'eqTo' instead", since = "1.0.2")
  def eqToVal[T](value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T]): T = eqTo(value)
}
