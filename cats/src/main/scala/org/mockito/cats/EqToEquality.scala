package org.mockito.cats

import cats.Eq
import cats.implicits._
import org.scalactic.Equality

class EqToEquality[T: Eq] extends Equality[T] {
  override def areEqual(a: T, b: Any): Boolean =
    (a == null && b == null) || (
      a != null &&
      b != null &&
      b.getClass == a.getClass &&
      a === b.asInstanceOf[T]
    )
}
