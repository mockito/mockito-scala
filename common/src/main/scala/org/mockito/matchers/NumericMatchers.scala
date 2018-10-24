package org.mockito.matchers

import org.mockito.ArgumentMatcher

/**
  * I transform everything to BigDecimal so any kind of number type can be compared
  */
class N {

  import ThatMatchers.argThat

  def >[N: Numeric](n: N): N =
    argThat[N](new ArgumentMatcher[N] {
      private val expected                = BigDecimal(n.toString)
      override def matches(v: N): Boolean = BigDecimal(v.toString) > expected
      override def toString: String       = s"n > $expected"
    })

  def >=[N: Numeric](n: N): N =
    argThat[N](new ArgumentMatcher[N] {
      private val expected                = BigDecimal(n.toString)
      override def matches(v: N): Boolean = BigDecimal(v.toString) >= expected
      override def toString: String       = s"n >= $expected"
    })

  def <[N: Numeric](n: N): N =
    argThat[N](new ArgumentMatcher[N] {
      private val expected                = BigDecimal(n.toString)
      override def matches(v: N): Boolean = BigDecimal(v.toString) < expected
      override def toString: String       = s"n < $expected"
    })

  def <=[N: Numeric](n: N): N =
    argThat[N](new ArgumentMatcher[N] {
      private val expected                = BigDecimal(n.toString)
      override def matches(v: N): Boolean = BigDecimal(v.toString) <= expected
      override def toString: String       = s"n <= $expected"
    })
}

private[mockito] trait NumericMatchers {

  val n = new N
}
