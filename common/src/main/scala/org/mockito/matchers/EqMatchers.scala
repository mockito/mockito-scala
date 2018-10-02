package org.mockito.matchers

import org.mockito.{ArgumentMatchers => JavaMatchers}

import scala.reflect.ClassTag

private[mockito] trait EqMatchers {

  /**
   * Delegates to <code>ArgumentMatchers.eq()</code>, it renames the method to <code>eqTo</code> to
   * avoid clashes with the Scala <code>eq</code> method used for reference equality
   *
   */
  def eqTo[T](value: T): T = JavaMatchers.eq(value)

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
  def isA[T](implicit classTag: ClassTag[T]): T = JavaMatchers.isA(classTag.runtimeClass.asInstanceOf[Class[T]])

  /**
   * Delegates to <code>ArgumentMatchers.refEq()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   *
   */
  def refEq[T](value: T, excludeFields: String*): T = JavaMatchers.refEq(value, excludeFields: _*)
}

private[mockito] object EqMatchers extends EqMatchers