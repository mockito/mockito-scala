package org.mockito.matchers

trait MacroBasedMatchers {

  /**
    * Wraps the standard 'ArgumentMatchers.eq()' matcher on the value class provided, this one requires the type to be explicit
    */
  def eqToVal[T](value: Any)(implicit valueClassMatchers: ValueClassMatchers[T]): T = valueClassMatchers.eqToVal(value)

  /**
    * Wraps the standard 'any' matcher on the value class provided, this one requires the type to be explicit
    */
  def anyVal[T](implicit valueClassMatchers: ValueClassMatchers[T]): T = valueClassMatchers.anyVal

}
