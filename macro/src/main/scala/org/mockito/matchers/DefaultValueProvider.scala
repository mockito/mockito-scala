package org.mockito.matchers

/**
 * Runtime support for providing default values. Shared across Scala 2 and Scala 3.
 */
trait DefaultValueProvider[T] {
  def default: T
}

object DefaultValueProvider extends DefaultValueProviderCompat {
  def defaultProvider[T]: DefaultValueProvider[T] =
    new DefaultValueProvider[T] {
      override def default: T = null.asInstanceOf[T]
    }
}
