package org.mockito.captor

import org.mockito.MockitoSugar

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.reflect.ClassTag

class Captor[T: ClassTag] extends ArgCaptor[T] {

  private val argumentCaptor = MockitoSugar.argumentCaptor[T]

  override def capture: T = argumentCaptor.capture()

  override def value: T = argumentCaptor.getValue

  override def values: List[T] = argumentCaptor.getAllValues.asScala.toList
}

object Captor {
  def apply[T: ClassTag]: ArgCaptor[T] = new Captor[T]
}

object ValCaptor {
  def apply[T](implicit c: ArgCaptor[T]): ArgCaptor[T] = c
}
