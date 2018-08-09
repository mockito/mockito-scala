package org.mockito.captor

import org.mockito.MockitoSugar
import org.mockito.internal.matchers.Equals

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.reflect.ClassTag

class Captor[T: ClassTag] {

  private val argumentCaptor = MockitoSugar.argumentCaptor[T]

  def capture: T = argumentCaptor.capture()

  def value: T = argumentCaptor.getValue

  def values: List[T] = argumentCaptor.getAllValues.asScala.toList

  def ===(expectation: T): Unit =
    if (!new Equals(expectation).matches(value)) throw new AssertionError(s"Got [$value] instead of [$expectation]")
}

object Captor {
  def apply[T: ClassTag]: Captor[T] = new Captor[T]

  implicit def asCapture[T](c: Captor[T]): T = c.capture
}
