package org.mockito
package matchers

import org.mockito.internal.ValueClassExtractor
import org.mockito.ArgumentMatchers as JavaMatchers
import org.scalactic.{ Equality, Prettifier }

import scala.reflect.ClassTag

private[mockito] trait EqMatchers {

  /**
   * Delegates to <code>ArgumentMatchers.same()</code>, it's only here so we expose all the `ArgumentMatchers` on a single place
   */
  def same[T](value: T): T = JavaMatchers.same(value)

  /**
   * Delegates to <code>ArgumentMatchers.isA(type: Class[T])</code> It provides a nicer API as you can, for instance, do isA[String] instead of isA(classOf[String])
   */
  def isA[T: ClassTag]: T = JavaMatchers.isA(clazz)

  /**
   * Delegates to <code>ArgumentMatchers.refEq()</code>, it's only here so we expose all the `ArgumentMatchers` on a single place
   */
  def refEq[T](value: T, excludeFields: String*): T = JavaMatchers.refEq(value, excludeFields*)

  /**
   * Creates a matcher that delegates on {{org.scalactic.Equality}} so you can always customise how the values are compared Also works with value classes
   */
  def eqTo[T: Equality: ValueClassExtractor](value: T)(implicit $pt: Prettifier): T = {
    JavaMatchers.argThat(new EqTo[T](value))
    value
  }

}
