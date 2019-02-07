package org.mockito
import scala.collection.mutable

package object matchers {
  trait VarargAwareArgumentMatcher[T] extends ArgumentMatcher[T] {
    override def matches(argument: T): Boolean = argument match {
      case a: mutable.WrappedArray[_] if a.length == 1 => doesMatch(a.head.asInstanceOf[T])
      case other => doesMatch(other)
    }

    def doesMatch(argument: T): Boolean
  }
}
