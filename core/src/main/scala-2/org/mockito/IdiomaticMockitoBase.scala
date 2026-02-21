package org.mockito

object IdiomaticMockitoBase extends IdiomaticMockitoBaseRuntime {

  case class ReturnedBy[T]() {
    def by[S](stubbing: S)(implicit $ev: T <:< S): S = macro DoSomethingMacro.returnedBy[T, S]
  }

  case class AnsweredBy[T]() {
    def by[S](stubbing: S)(implicit $ev: T <:< S): S = macro DoSomethingMacro.answeredBy[T, S]
  }

  class ThrownBy[E] {
    def by[T](stubbing: T)(implicit $ev: E <:< Throwable): T = macro DoSomethingMacro.thrownBy[T]
  }
}

trait IdiomaticMockitoBase extends IdiomaticStubbing with PostfixVerifications
