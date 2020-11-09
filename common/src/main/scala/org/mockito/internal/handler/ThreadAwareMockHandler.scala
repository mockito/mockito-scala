package org.mockito.internal.handler

import org.mockito.invocation.{ Invocation, InvocationContainer, MockHandler }
import org.mockito.mock.MockCreationSettings
import org.scalactic.Prettifier

class ThreadAwareMockHandler[T](settings: MockCreationSettings[T])(implicit $pt: Prettifier) extends MockHandler[T] {
  private val currentThread = Thread.currentThread()
  private val delegate      = ScalaMockHandler(settings)

  override def handle(invocation: Invocation): AnyRef =
    if (Thread.currentThread() == currentThread) delegate.handle(invocation)
    else invocation.callRealMethod()

  override def getMockSettings: MockCreationSettings[T] = delegate.getMockSettings

  override def getInvocationContainer: InvocationContainer = delegate.getInvocationContainer
}

object ThreadAwareMockHandler {
  def apply[T](settings: MockCreationSettings[T])(implicit $pt: Prettifier): ThreadAwareMockHandler[T] =
    new ThreadAwareMockHandler(settings)($pt)
}
