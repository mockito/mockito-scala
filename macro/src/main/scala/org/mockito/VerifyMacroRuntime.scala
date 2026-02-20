package org.mockito

import org.mockito.internal.verification.VerificationModeFactory
import org.mockito.verification.VerificationMode

/**
 * Runtime support classes for VerifyMacro. These classes are used by macro-generated code and shared across Scala 2 and Scala 3.
 */
object VerifyMacroRuntime {

  object Never extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.never
  }

  object NeverAgain extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = VerificationModeFactory.noMoreInteractions()
  }

  object Once extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.times(1)
  }
}

trait ScalaVerificationMode {
  def verificationMode: VerificationMode
}

sealed trait VerifyOrder {
  def verify[T](mock: T): T
  def verifyWithMode[T](mock: T, mode: ScalaVerificationMode): T
}

object VerifyUnOrdered extends VerifyOrder {
  override def verify[T](mock: T): T                                      = Mockito.verify(mock)
  override def verifyWithMode[T](mock: T, mode: ScalaVerificationMode): T = Mockito.verify(mock, mode.verificationMode)
}

case class VerifyInOrder(mocks: Seq[AnyRef]) extends VerifyOrder {
  private val _inOrder = Mockito.inOrder(mocks*)

  override def verify[T](mock: T): T                                      = _inOrder.verify(mock)
  override def verifyWithMode[T](mock: T, mode: ScalaVerificationMode): T = _inOrder.verify(mock, mode.verificationMode)
  def verifyNoMoreInteractions(): Unit                                    = _inOrder.verifyNoMoreInteractions()
}

object VerifyOrder {
  implicit val unOrdered: VerifyOrder = VerifyUnOrdered
}
