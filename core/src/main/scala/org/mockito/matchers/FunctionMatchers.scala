package org.mockito.matchers
import org.mockito.ArgumentMatcher

import org.mockito.{ ArgumentMatchers => JavaMatchers }

private[mockito] trait FunctionMatchers {

  def function0[T](value: T): () => T =
    JavaMatchers.argThat(new ArgumentMatcher[() => T] {

      override def matches(argument: () => T): Boolean = argument() == value

      override def toString: String = s"() => $value"
    })

}
