package org.mockito.matchers

private[mockito] trait FunctionMatchers {
  def function0[T](value: T): () => T = ThatMatchers.argThat((f: () => T) => f() == value, s"() => $value")
}
