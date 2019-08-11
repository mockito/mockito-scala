package org.mockito
package internal.handler

import org.mockito.internal.util.Primitives.defaultValue
import org.mockito.invocation.{ Invocation, InvocationContainer, MockHandler }
import org.mockito.mock.MockCreationSettings

class ScalaNullResultGuardian[T](delegate: MockHandler[T]) extends MockHandler[T] {

  override def handle(invocation: Invocation): AnyRef = {
    val result     = delegate.handle(invocation)
    val returnType = invocation.returnType
    if (result == null && returnType.isPrimitive)
      defaultValue(returnType).asInstanceOf[AnyRef]
    else
      result
  }

  override def getMockSettings: MockCreationSettings[T]    = delegate.getMockSettings
  override def getInvocationContainer: InvocationContainer = delegate.getInvocationContainer
}
