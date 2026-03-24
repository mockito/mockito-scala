package org.mockito

object Called {
  inline def by[T](inline stubbing: T): T = ${ DoSomethingMacro.calledByImpl[T]('stubbing) }
}
