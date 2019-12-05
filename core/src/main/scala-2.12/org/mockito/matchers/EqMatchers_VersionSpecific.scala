package org.mockito.matchers

import org.mockito.internal.ValueClassExtractor
import org.mockito.{ ArgumentMatchers => JavaMatchers }
import org.scalactic.{ Equality, Prettifier }

trait EqMatchers_VersionSpecific {
  /**
   * Creates a matcher that delegates on {{org.scalactic.Equality}} so you can always customise how the values are compared
   * Also works with value classes
   */
  def eqTo[T: Equality: ValueClassExtractor](value: T)(implicit $pt: Prettifier): T = {
    JavaMatchers.argThat(new EqTo[T](value))
    value
  }

  /**
   * It was intended to be used instead of eqTo when the argument is a value class,
   * but eqTo now supports value classes so it is not needed anymore
   */
  @deprecated("Use 'eqTo' instead", since = "1.0.2")
  def eqToVal[T: Equality: ValueClassExtractor](value: T)(implicit $pt: Prettifier): T = eqTo(value)
}
