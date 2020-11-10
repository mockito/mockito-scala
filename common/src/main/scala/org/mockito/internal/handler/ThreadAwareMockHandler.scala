package org.mockito.internal.handler

import org.mockito.AdditionalAnswers
import org.mockito.invocation.{ Invocation, InvocationContainer, MockHandler }
import org.mockito.mock.MockCreationSettings
import org.scalactic.Prettifier

class ThreadAwareMockHandler[T](settings: MockCreationSettings[T], realImpl: T)(implicit $pt: Prettifier) extends MockHandler[T] {
  private val currentThread    = Thread.currentThread()
  private val mockDelegate     = ScalaMockHandler(settings)
  private val realImplDelegate = AdditionalAnswers.delegatesTo(realImpl)

  override def handle(invocation: Invocation): AnyRef =
    if (Thread.currentThread() == currentThread) mockDelegate.handle(invocation)
    else realImplDelegate.answer(invocation)

  override def getMockSettings: MockCreationSettings[T] = mockDelegate.getMockSettings

  override def getInvocationContainer: InvocationContainer = mockDelegate.getInvocationContainer
}

object ThreadAwareMockHandler {
  def apply[T](settings: MockCreationSettings[T], realImpl: T)(implicit $pt: Prettifier): ThreadAwareMockHandler[T] =
    new ThreadAwareMockHandler(settings, realImpl)($pt)
}
