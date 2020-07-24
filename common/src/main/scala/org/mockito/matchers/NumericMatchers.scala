package org.mockito.matchers

import org.mockito.ArgumentMatcher
import org.scalactic.Tolerance
import org.scalactic.TripleEqualsSupport.Spread

/**
 * I transform everything to BigDecimal so any kind of number type can be compared
 */
class NumericMatcher[N](n: N, name: String, comparison: (BigDecimal, BigDecimal) => Boolean) extends ArgumentMatcher[N] with Serializable {
  private val expected                = BigDecimal(n.toString)
  override def matches(v: N): Boolean = comparison(BigDecimal(v.toString), expected)
  override def toString: String       = s"n $name $expected"
}

class N {
  import ThatMatchers.argThat

  /**
   * Creates a matcher that works only if there is a Numeric[T] associated with the type, this allows you to write stuff like
   *
   *     aMock.pepe(4.1)
   *     aMock.pepe(n > 4) was called
   */
  def >[N: Numeric](n: N): N = argThat[N](new NumericMatcher(n, ">", _ > _))

  /**
   * Creates a matcher that works only if there is a Numeric[T] associated with the type, this allows you to write stuff like
   *
   *     aMock.pepe(4)
   *     aMock.pepe(n >= 4) was called
   */
  def >=[N: Numeric](n: N): N = argThat[N](new NumericMatcher(n, ">=", _ >= _))

  /**
   * Creates a matcher that works only if there is a Numeric[T] associated with the type, this allows you to write stuff like
   *
   *     aMock.pepe(3.1)
   *     aMock.pepe(n < 4) was called
   */
  def <[N: Numeric](n: N): N = argThat[N](new NumericMatcher(n, "<", _ < _))

  /**
   * Creates a matcher that works only if there is a Numeric[T] associated with the type, this allows you to write stuff like
   *
   *     aMock.pepe(4)
   *     aMock.pepe(n <= 4) was called
   */
  def <=[N: Numeric](n: N): N = argThat[N](new NumericMatcher(n, "<=", _ <= _))

  /**
   * Creates a matcher that delegates on {{org.scalactic.TripleEqualsSupport.Spread}} so you can get around the lack of
   * precision on floating points, e.g.
   *
   *     aMock.barDouble(4.999)
   *     verify(aMock).barDouble(=~(5.0 +- 0.001))
   */
  def =~[T](spread: Spread[T]): T = ThatMatchers.argThat[T](spread.isWithin _, s"=~($spread)")
}

private[mockito] trait NumericMatchers extends Tolerance {

  /**
   * Provides a starting point to write expressions like n > 3, etc
   */
  val n = new N
}
