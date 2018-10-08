package org.mockito.captor

import org.mockito._

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.reflect.ClassTag

private class WrapperCaptor[T: ClassTag] extends Captor[T] {

  private val argumentCaptor: ArgumentCaptor[T] = ArgumentCaptor.forClass(clazz)

  override def capture: T = argumentCaptor.capture()

  override def value: T = argumentCaptor.getValue

  override def values: List[T] = argumentCaptor.getAllValues.asScala.toList
}

object ArgCaptor {
  def apply[T: ClassTag]: Captor[T] = new WrapperCaptor[T]
}

object ValCaptor {
  def apply[T](implicit c: Captor[T]): Captor[T] = c
}
