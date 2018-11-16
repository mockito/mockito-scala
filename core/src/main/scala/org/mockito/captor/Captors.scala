package org.mockito.captor

object ArgCaptor {
  def apply[T](implicit c: Captor[T]): Captor[T] = c
}

object ValCaptor {
  @deprecated("use 'ArgCaptor' instead", since = "1.0.2")
  def apply[T](implicit c: Captor[T]): Captor[T] = c
}
