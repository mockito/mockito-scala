package org.mockito
import scala.collection.immutable.ArraySeq

package object matchers {
  trait VarargAwareArgumentMatcher[T] extends ArgumentMatcher[T] {
    override def matches(argument: T): Boolean = argument match {
      case a: ArraySeq[_] if a.length == 1 => doesMatch(a.head.asInstanceOf[T])
      case other => doesMatch(other)
    }

    def doesMatch(argument: T): Boolean
  }
}
