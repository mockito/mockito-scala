package org.mockito

import org.mockito.internal.ValueClassExtractor
import org.mockito.stubbing.Stubber

import scala.annotation.targetName

private[mockito] trait DoSomethingCompat {
  @targetName("doAnswerFunction0")
  def doAnswer[R: ValueClassExtractor](f: () => R): Stubber =
    Mockito.doAnswer(invocationToAnswer[R](_ => f()))
}
