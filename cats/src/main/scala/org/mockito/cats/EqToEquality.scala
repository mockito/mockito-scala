package org.mockito.cats

import cats.Eq
import cats.implicits._
import org.mockito._
import org.scalactic.Equality

import scala.reflect.ClassTag

class EqToEquality[T: ClassTag: Eq] extends Equality[T] {
  override def areEqual(a: T, b: Any): Boolean = clazz[T].isInstance(b) && a === b.asInstanceOf[T]
}
