package org.mockito
package internal.stubbing.answers

import org.mockito.internal.configuration.plugins.Plugins
import org.mockito.internal.exceptions.Reporter.cannotStubWithNullThrowable
import org.mockito.invocation.InvocationOnMock

import scala.reflect.ClassTag

class ScalaThrowsException(t: Throwable) extends ThrowsException(t) {
  override def validateFor(invocation: InvocationOnMock): Unit = if (t == null) throw cannotStubWithNullThrowable
}

object ScalaThrowsException {
  def apply(t: Throwable): ScalaThrowsException = new ScalaThrowsException(t)

  def apply[T <: Throwable: ClassTag]: ScalaThrowsException =
    new ScalaThrowsException(Plugins.getInstantiatorProvider.getInstantiator(null).newInstance(clazz))
}
