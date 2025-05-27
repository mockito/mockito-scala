package org.mockito.matchers

import org.mockito.internal.ValueClassExtractor
import org.mockito.{ ArgumentMatchers => JavaMatchers }
import org.scalactic.{ Equality, Prettifier }

trait EqMatchers_VersionSpecific {

  /**
   * Creates a matcher that delegates on {{org.scalactic.Equality}} so you can always customise how the values are compared Also works with value classes
   */
  def eqTo[T: Equality: ValueClassExtractor](value: T)(implicit $pt: Prettifier): T = {
    JavaMatchers.argThat(new EqTo[T](value))
    value
  }

}
