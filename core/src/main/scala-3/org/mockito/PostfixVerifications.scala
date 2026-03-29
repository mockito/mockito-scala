package org.mockito

import scala.quoted.*

/**
 * Scala 3 version of PostfixVerifications with inline macro-based operations (stubs).
 */
trait PostfixVerifications extends PostfixVerificationsRuntime {

  import org.mockito.IdiomaticMockitoBaseRuntime.*

  extension [T](inline stubbing: T) {
    transparent inline def was(called: Called.type)(using order: VerifyOrder): Verification =
      ${ VerifyMacro.wasMacroImpl[T, Verification]('stubbing, 'called, 'order) }

    transparent inline def wasNever(called: Called.type)(using order: VerifyOrder): Verification =
      ${ VerifyMacro.wasNeverMacroImpl[T, Verification]('stubbing, 'called, 'order) }

    transparent inline def wasNever(inline called: CalledAgain)(using $ev: T <:< AnyRef): Verification =
      ${ VerifyMacro.wasNeverCalledAgainMacroImpl[T, Verification]('stubbing, 'called) }

    transparent inline def wasCalled(called: ScalaVerificationMode)(using order: VerifyOrder): Verification =
      ${ VerifyMacro.wasCalledMacroImpl[T, Verification]('stubbing, 'called, 'order) }
  }

  def InOrder(mocks: AnyRef*)(verifications: VerifyInOrder => Verification): Verification = verifications(VerifyInOrder(mocks))
}
