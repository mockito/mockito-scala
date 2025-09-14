package org.mockito
package matchers

/**
 * The product (2-tuple) of two matchers
 */
case class ProductOf[A, B] private (ma: ArgumentMatcher[A], mb: ArgumentMatcher[B]) extends ArgumentMatcher[(A, B)] {
  override def matches(ab: (A, B)): Boolean = ab match { case (a, b) => ma.matches(a) && mb.matches(b) }
  override def toString: String             = s"productOf($ma, $mb)"

  // Address "-Xsource:3" warning
  @deprecated("for bincompat only, do not use", "2.0.1")
  private[mockito] def copy(ma: ArgumentMatcher[A] = this.ma, mb: ArgumentMatcher[B] = this.mb): ProductOf[A, B] =
    new ProductOf(ma, mb)
}

object ProductOf {
  def apply[A, B](ma: ArgumentMatcher[A], mb: ArgumentMatcher[B]): ArgumentMatcher[(A, B)] =
    new ProductOf(ma, mb)
}
