package org.mockito.matchers

private[mockito] trait StringThatMatchers {
  import ThatMatchers.argThat

  /**
   * Delegates to <code>ArgumentMatchers.matches()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   */
  def matches(regex: String): String = argThat((s: String) => s.matches(regex), s"matches($regex)")

  /**
   * Delegates to <code>ArgumentMatchers.startsWith()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   */
  def startsWith(prefix: String): String = argThat((s: String) => s.startsWith(prefix), s"startsWith($prefix)")

  /**
   * Delegates to <code>ArgumentMatchers.contains()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   */
  def contains(substring: String): String = argThat((s: String) => s.contains(substring), s"contains($substring)")

  /**
   * Delegates to <code>ArgumentMatchers.endsWith()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place
   */
  def endsWith(suffix: String): String = argThat((s: String) => s.endsWith(suffix), s"endsWith($suffix)")
}
