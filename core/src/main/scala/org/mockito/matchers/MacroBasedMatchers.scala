package org.mockito.matchers

import scala.language.experimental.macros

trait MacroBasedMatchers {

  /**
   * To be used instead of eqTo when the argument is a value class
   */
  def eqToVal[T](value: T): T = macro ValueClassMatchers.eqToValMatcher[T]

  /**
    * To be used instead of any when the argument is a value class
    */
  def anyVal[T]: T = macro ValueClassMatchers.anyValMatcher[T]

}
