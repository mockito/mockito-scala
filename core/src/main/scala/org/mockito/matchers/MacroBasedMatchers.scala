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
    * It was intended to be used instead of any when the argument is a value class,
    * but any now supports value classes so it is not needed anymore
    */
  @deprecated("Use 'any[T]' or '*[T]' instead", since = "1.0.2")
  def anyVal[T](implicit $m: AnyMatcher[T]): T = $m.any

  /**
    * Delegates to <code>ArgumentMatchers.any()</code>, it's main purpose is to remove the () out of
    * the method call, if you try to do that directly on the test you get this error
    *
    * Error:(71, 46) polymorphic expression cannot be instantiated to expected type;
    * found   : [T]()T
    * required: String
    * when you try to something like ArgumentMatchers.any
    *
    * It also fixes the NullPointerException when used on an value class argument (IMPORTANT: YOU MUST PROVIDE THE TYPE FOR VALUE CLASSES)
    *
    */
  def any[T](implicit $m: AnyMatcher[T]): T = $m.any

  /**
    * Alias for [[org.mockito.matchers.MacroBasedMatchers.any]]
    */
  def *[T](implicit $m: AnyMatcher[T]): T = $m.any
}
