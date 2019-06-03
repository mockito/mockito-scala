package org.mockito.stubbing

import org.mockito.invocation.InvocationOnMock

trait ScalaAnswer[T] extends Answer[T] with Serializable { self =>
  def andThen[A](g: T => A): ScalaAnswer[A] = new ScalaAnswer[A] {
    override def answer(invocation: InvocationOnMock): A = g(self.answer(invocation))
  }
}

object ScalaAnswer {
  def lift[T](f: InvocationOnMock => T): ScalaAnswer[T] = new ScalaAnswer[T] {
    override def answer(invocation: InvocationOnMock): T = f(invocation)
  }
}
