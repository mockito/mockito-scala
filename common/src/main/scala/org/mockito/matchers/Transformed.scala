package org.mockito
package matchers

/** Matcher tranformed from one type to another with a function to modify the input
 *
 * Technically this is 'contramapped' but that seemed like an unnecessarily jargony name.
 */
case class Transformed[A, B] private (ma: ArgumentMatcher[A])(f: B => A) extends ArgumentMatcher[B] {
  override def matches(b: B) = ma.matches(f(b))
  override def toString      = s"transformed($ma: $f)"
}

object Transformed {
  def apply[A, B](ma: ArgumentMatcher[A])(f: B => A): ArgumentMatcher[B] =
    new Transformed(ma)(f)
}
