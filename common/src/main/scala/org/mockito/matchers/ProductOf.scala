package org.mockito
package matchers

/** The product (2-tuple) of two matchers
 */
case class ProductOf[A, B] private (ma: ArgumentMatcher[A], mb: ArgumentMatcher[B]) extends ArgumentMatcher[(A, B)] {
  override def matches(ab: (A, B)) = ab match { case (a, b) => ma.matches(a) && mb.matches(b) }
  override def toString            = s"productOf($ma, $mb)"
}

object ProductOf {
  def apply[A, B](ma: ArgumentMatcher[A], mb: ArgumentMatcher[B]): ArgumentMatcher[(A, B)] =
    new ProductOf(ma, mb)
}
