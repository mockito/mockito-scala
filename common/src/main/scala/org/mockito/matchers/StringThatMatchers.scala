package org.mockito.matchers

import org.mockito.{ArgumentMatchers => JavaMatchers}

private[mockito] trait StringThatMatchers {

  /**
   * Delegates to <code>ArgumentMatchers.matches()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   *
   */
  def matches(regex: String): String = JavaMatchers.matches(regex)

  /**
   * Delegates to <code>ArgumentMatchers.startsWith()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   *
   */
  def startsWith(prefix: String): String = JavaMatchers.startsWith(prefix)

  /**
   * Delegates to <code>ArgumentMatchers.contains()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   *
   */
  def contains(substring: String): String = JavaMatchers.contains(substring)

  /**
   * Delegates to <code>ArgumentMatchers.endsWith()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   *
   */
  def endsWith(suffix: String): String = JavaMatchers.endsWith(suffix)
}
