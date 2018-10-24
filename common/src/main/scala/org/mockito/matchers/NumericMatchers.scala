package org.mockito.matchers

import org.mockito.ArgumentMatcher

/**
 * I transform everything to BigDecimal so any kind of number type can be compared
 */
class NumericMatcher[N](n: N, name: String, comparison: (BigDecimal, BigDecimal) => Boolean) extends ArgumentMatcher[N] {
  private val expected                = BigDecimal(n.toString)
  override def matches(v: N): Boolean = comparison(BigDecimal(v.toString), expected)
  override def toString: String       = s"n $name $expected"
}

class N {

  import ThatMatchers.argThat

  def >[N: Numeric](n: N): N = argThat[N](new NumericMatcher(n, ">", _ > _))

  def >=[N: Numeric](n: N): N = argThat[N](new NumericMatcher(n, ">=", _ >= _))

  def <[N: Numeric](n: N): N = argThat[N](new NumericMatcher(n, "<", _ < _))

  def <=[N: Numeric](n: N): N = argThat[N](new NumericMatcher(n, "<=", _ <= _))
}

private[mockito] trait NumericMatchers {

  /**
   * Provides a starting point to write expressions like n > 3, etc
   */
  val n = new N
}
