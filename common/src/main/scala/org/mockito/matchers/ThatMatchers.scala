package org.mockito.matchers

import org.mockito.{ ArgumentMatcher, ArgumentMatchers => JavaMatchers }

private[mockito] trait ThatMatchers {

  /**
   * Delegates to <code>ArgumentMatchers.argThat(matcher)</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   *
   */
  def argThat[T](matcher: ArgumentMatcher[T]): T = JavaMatchers.argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitives", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   *
   */
  def byteThat(matcher: ArgumentMatcher[Byte]): Byte = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   *
   */
  def booleanThat(matcher: ArgumentMatcher[Boolean]): Boolean = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   *
   */
  def charThat(matcher: ArgumentMatcher[Char]): Char = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   *
   */
  def doubleThat(matcher: ArgumentMatcher[Double]): Double = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   *
   */
  def intThat(matcher: ArgumentMatcher[Int]): Int = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   *
   */
  def floatThat(matcher: ArgumentMatcher[Float]): Float = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   *
   */
  def shortThat(matcher: ArgumentMatcher[Short]): Short = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary conversion that would be necessary used
   * the Java version
   *
   */
  def longThat(matcher: ArgumentMatcher[Long]): Long = argThat(matcher)

  def argMatching[T](pf: PartialFunction[Any, Unit]) =
    argThat[T](new ArgumentMatcher[T] {
      override def matches(argument: T): Boolean = pf.isDefinedAt(argument)
      override def toString: String = "argMatching(...)"
    })
}

private[mockito] object ThatMatchers extends ThatMatchers
