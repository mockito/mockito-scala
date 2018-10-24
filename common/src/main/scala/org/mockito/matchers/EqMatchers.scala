package org.mockito
package matchers

import org.mockito.{ArgumentMatchers => JavaMatchers}
import org.scalactic.Equality
import org.scalactic.TripleEqualsSupport.Spread

import scala.reflect.ClassTag

private[mockito] trait EqMatchers {

  /**
   * Delegates to <code>ArgumentMatchers.eq()</code>, it renames the method to <code>eqTo</code> to
   * avoid clashes with the Scala <code>eq</code> method used for reference equality
   *
   */
  def eqTo[T](value: T)(implicit $eq: Equality[T]): T =
    ThatMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = $eq.areEqual(v, value)
      override def toString: String = s"equality($value)"
    })

  def =~[T](spread: Spread[T]): T =
    ThatMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = spread.isWithin(v)
      override def toString: String = s"aprox($spread)"
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
