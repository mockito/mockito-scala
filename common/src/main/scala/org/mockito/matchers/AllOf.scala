package org.mockito
package matchers

/** Combine multiple matchers using AND
 */
case class AllOf[A] private (matchers: List[ArgumentMatcher[A]]) extends ArgumentMatcher[A] {
  override def matches(a: A) = matchers.forall(_.matches(a))

  override def toString =
    matchers match {
      case Nil            => "<any>"
      case matcher :: Nil => matcher.toString
      case _              => matchers.mkString("allOf(", ", ", ")")
    }
}

object AllOf {
  def apply[A](matchers: ArgumentMatcher[A]*): ArgumentMatcher[A] =
    new AllOf(matchers.flatMap {
      case AllOf(ms) => ms
      case m         => List(m)
    }.toList)
}
