package org.mockito.matchers

import org.mockito.{ ArgumentMatchers => JavaMatchers }

trait MacroBasedMatchers {

  /**
   * It was intended to be used instead of any when the argument is a value class,
   * but any now supports value classes so it is not needed anymore
   */
  @deprecated("Use 'any[T]' or '*[T]' instead", since = "1.0.2")
  def anyVal[T](implicit default: DefaultValueProvider[T]): T = any[T]

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
   */
  def any[T](implicit default: DefaultValueProvider[T]): T = {
    JavaMatchers.any[T]()
    default.default
  }

  /**
   * Alias for [[org.mockito.matchers.MacroBasedMatchers.any]]
   */
  def *[T](implicit default: DefaultValueProvider[T]): T = any[T]
}
