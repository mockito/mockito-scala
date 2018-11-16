package org.mockito.matchers

import org.scalactic.Equality

import scala.language.experimental.macros

trait MacroBasedMatchers {

  /**
    * Creates a matcher that delegates on {{org.scalactic.Equality}} so you can always customise how the values are compared
    * Also works with value classes
    */
  def eqTo[T](value: T)(implicit eq: Equality[T]): T = macro MacroMatchers.eqToMatcher[T]

  /**
    * It was intended to be used instead of eqTo when the argument is a value class,
    * but eqTo now supports value classes so it is not needed anymore
    */
  @deprecated("Use 'eqTo' instead", since = "1.0.2")
  def eqToVal[T](value: T): T = macro MacroMatchers.eqToValMatcher[T]

  /**
   * To be used instead of any when the argument is a value class
   */
  def anyVal[T]: T = macro MacroMatchers.anyValMatcher[T]

}
