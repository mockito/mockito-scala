package org.mockito

trait PostfixVerifications extends PostfixVerificationsRuntime {

  import org.mockito.IdiomaticMockitoBase.*

  implicit class VerifyingOps[T](stubbing: T) {
    def was(called: Called.type)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacro[T, Verification]

    def wasNever(called: Called.type)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacro[T, Verification]

    def wasNever(called: CalledAgain)(implicit $ev: T <:< AnyRef): Verification =
      macro VerifyMacro.wasNeverCalledAgainMacro[T, Verification]

    def wasCalled(called: ScalaVerificationMode)(implicit order: VerifyOrder): Verification = macro VerifyMacro.wasMacro[T, Verification]
  }

  def InOrder(mocks: AnyRef*)(verifications: VerifyInOrder => Verification): Verification = verifications(VerifyInOrder(mocks))

}
