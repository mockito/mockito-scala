package org.mockito

import scala.quoted.*

/**
 * Scala 3 version of IdiomaticMockitoBase with inline macro-based operations (stubs).
 */
object IdiomaticMockitoBase extends IdiomaticMockitoBaseRuntime {
  // Macro-based case classes - value is captured from the willBe/mustBe call
  case class ReturnedBy[T](value: T) {
    transparent inline def by[S](inline stubbing: S): S =
      DoSomethingMacro.returnedByMacro[T, S](value, stubbing)
  }

  case class AnsweredBy[T](valueThunk: () => T) {
    transparent inline def by[S](inline stubbing: S): S =
      DoSomethingMacro.answeredByThunkMacro[T, S](valueThunk, stubbing)
  }

  class ThrownBy[E](value: E) {
    transparent inline def by[T](inline stubbing: T): T =
      DoSomethingMacro.thrownByMacro[T, E](value, stubbing)
  }
}

trait IdiomaticMockitoBase extends IdiomaticStubbing with PostfixVerifications
