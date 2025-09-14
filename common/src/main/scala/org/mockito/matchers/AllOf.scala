package org.mockito
package matchers

/**
 * Combine multiple matchers using AND
 */
case class AllOf[A] private (matchers: List[ArgumentMatcher[A]]) extends ArgumentMatcher[A] {
  override def matches(a: A): Boolean = matchers.forall(_.matches(a))

  override def toString: String =
    matchers match {
      case Nil            => "<any>"
      case matcher :: Nil => matcher.toString
      case _              => matchers.mkString("allOf(", ", ", ")")
    }

  // Address "-Xsource:3" warning
  @deprecated("for bincompat only, do not use", "2.0.1")
  private[mockito] def copy(matchers: List[ArgumentMatcher[A]] = this.matchers): AllOf[A] = new AllOf(matchers)
}

object AllOf {
  def apply[A](matchers: ArgumentMatcher[A]*): ArgumentMatcher[A] =
    new AllOf(matchers.flatMap {
      case AllOf(ms) => ms
      case m         => List(m)
    }.toList)

  // Address "-Xsource:3" warning
  @deprecated("for bincompat only, do not use", "2.0.1")
  private[mockito] def apply[A](matchers: List[ArgumentMatcher[A]]): ArgumentMatcher[A] = apply(matchers*)
}
