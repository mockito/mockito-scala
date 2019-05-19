package org.mockito.scalaz

import org.scalactic.Equality
import scalaz.Equal
import scalaz.Scalaz._

class EqToEquality[T: Equal] extends Equality[T] {
  override def areEqual(a: T, b: Any): Boolean =
    (a == null && b == null) || (
      a != null &&
      b != null &&
      b.getClass == a.getClass &&
      a === b.asInstanceOf[T]
    )
}
