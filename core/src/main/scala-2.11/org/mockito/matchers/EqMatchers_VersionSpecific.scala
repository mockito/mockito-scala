package org.mockito.matchers

trait EqMatchers_VersionSpecific {
  /**
   * Creates a matcher that delegates on {{org.scalactic.Equality}} so you can always customise how the values are compared
   * Also works with value classes
   */
  def eqTo[T](value: T): T = macro MacroMatchers_211.eqToMatcher[T]

  /**
   * It was intended to be used instead of eqTo when the argument is a value class,
   * but eqTo now supports value classes so it is not needed anymore
   */
  @deprecated("Use 'eqTo' instead", since = "1.0.2")
  def eqToVal[T](value: T): T = macro MacroMatchers_211.eqToValMatcher[T]
}
