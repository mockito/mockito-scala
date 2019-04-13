package org.mockito.internal.stubbing.answers
import org.mockito.internal.exceptions.Reporter.cannotStubWithNullThrowable
import org.mockito.invocation.InvocationOnMock

class ScalaThrowsException(t: Throwable) extends ThrowsException(t) {
  override def validateFor(invocation: InvocationOnMock): Unit = if (t == null) throw cannotStubWithNullThrowable
}
