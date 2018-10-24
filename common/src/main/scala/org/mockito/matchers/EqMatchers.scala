package org.mockito
package matchers

import org.mockito.{ArgumentMatchers => JavaMatchers}
import org.scalactic.Equality
import org.scalactic.TripleEqualsSupport.Spread

import scala.reflect.ClassTag

private[mockito] trait EqMatchers {

  /**
   * Creates a matcher that delegates on {{org.scalactic.Equality}} so you can always customise how the values are compared
   */
  def eqTo[T](value: T)(implicit $eq: Equality[T]): T =
    ThatMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = $eq.areEqual(v, value)
      override def toString: String = s"eqTo($value)"
    })

  /**
    * Creates a matcher that delegates on {{org.scalactic.TripleEqualsSupport.Spread}} so you can get around the lack of
    * precision on floating points, e.g.
    *
    *     aMock.barDouble(4.999)
    *     verify(aMock).barDouble(=~(5.0 +- 0.001))
    *
    */
  def =~[T](spread: Spread[T]): T =
    ThatMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = spread.isWithin(v)
      override def toString: String = s"=~($spread)"
    })

  /**
   * Delegates to <code>ArgumentMatchers.same()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   *
   */
  def same[T](value: T): T = JavaMatchers.same(value)

  /**
   * Delegates to <code>ArgumentMatchers.isA(type: Class[T])</code>
   * It provides a nicer API as you can, for instance, do isA[String] instead of isA(classOf[String])
   *
   */
  def isA[T: ClassTag]: T = JavaMatchers.isA(clazz)

  /**
   * Delegates to <code>ArgumentMatchers.refEq()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   *
   */
  def refEq[T](value: T, excludeFields: String*): T = JavaMatchers.refEq(value, excludeFields: _*)
}
