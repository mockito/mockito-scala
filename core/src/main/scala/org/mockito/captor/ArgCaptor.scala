package org.mockito.captor

object ArgCaptor {
  def apply[T](implicit c: Captor[T]): Captor[T] = c
}
