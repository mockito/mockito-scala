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
   * To be used instead of any when the argument is a value class
   */
  def anyVal[T]: T = macro MacroMatchers.anyValMatcher[T]

}
