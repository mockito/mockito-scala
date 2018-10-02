package org.mockito.matchers

import org.mockito.{ArgumentMatchers => JavaMatchers}

private[mockito] trait NullMatchers {

  /**
   * Delegates to <code>ArgumentMatchers.isNull()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place, but marked as @deprecated as you shouldn't be testing for nulls
   * on Scala
   *
   */
  @deprecated
  def isNull[T]: T = JavaMatchers.isNull[T]

  /**
   * Delegates to <code>ArgumentMatchers.isNotNull()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place, but marked as @deprecated as you shouldn't be testing for nulls
   * on Scala
   *
   */
  @deprecated
  def isNotNull[T]: T = JavaMatchers.isNotNull[T]
}
