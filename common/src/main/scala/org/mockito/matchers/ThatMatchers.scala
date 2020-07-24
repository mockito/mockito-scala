package org.mockito.matchers

import org.mockito.{ ArgumentMatcher, ArgumentMatchers => JavaMatchers }

private[mockito] trait ThatMatchers {

  /**
   * Delegates to <code>ArgumentMatchers.argThat(matcher)</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   */
  def argThat[T](matcher: ArgumentMatcher[T]): T = argThat(matcher.matches, matcher.toString)

  /*
   * Overloaded version to avoid having to instantiate a matcher without using SAM to keep it compatible with 2.11,
   * It also adds support for varargs out of the box
   */
  def argThat[T](f: T => Boolean, desc: => String = "argThat(<condition>)"): T =
    JavaMatchers.argThat(new ArgumentMatcher[T] with Serializable {
      override def matches(argument: T): Boolean = f(argument)
      override def toString: String              = desc
    })

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitives", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   */
  def byteThat(matcher: ArgumentMatcher[Byte]): Byte = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   */
  def booleanThat(matcher: ArgumentMatcher[Boolean]): Boolean = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   */
  def charThat(matcher: ArgumentMatcher[Char]): Char = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   */
  def doubleThat(matcher: ArgumentMatcher[Double]): Double = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   */
  def intThat(matcher: ArgumentMatcher[Int]): Int = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   */
  def floatThat(matcher: ArgumentMatcher[Float]): Float = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary implicit conversion that would be necessary if we used
   * the Java version
   */
  def shortThat(matcher: ArgumentMatcher[Short]): Short = argThat(matcher)

  /**
   * Delegates the call to <code>argThat</code> but using the Scala "primitive", this
   * provides avoids an unnecessary conversion that would be necessary used
   * the Java version
   */
  def longThat(matcher: ArgumentMatcher[Long]): Long = argThat(matcher)

  /**
   * Creates a matcher that delegates on a partial function to enable syntax like
   *
   *       foo.bar(argMatching({ case Baz(n, _) if n > 90 => })) returns "mocked!"
   *       foo.bar(argMatching({ case Baz(_, "pepe") => })) was called
   */
  def argMatching[T](pf: PartialFunction[Any, Unit]) = argThat[T](pf.isDefinedAt(_), "argMatching(...)")
}

private[mockito] object ThatMatchers extends ThatMatchers
