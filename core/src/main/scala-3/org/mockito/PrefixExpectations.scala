package org.mockito

import scala.quoted.*

/**
 * Scala 3 version of PrefixExpectations with inline macro-based operations (stubs).
 */
trait PrefixExpectations extends PrefixExpectationsRuntime {
  import org.mockito.IdiomaticMockitoBase.*

  class ExpectationOps(val mode: ScalaVerificationMode) {

    /**
     * Use `calls to` to describe expectations about a _stubbed method call_.
     *
     * If you need to describe expectations about a mocked object itself (i.e. zero interactions), use `calls on`.
     */
    transparent inline def to[T](inline stubbedMethodCall: T)(using inline order: VerifyOrder): Verification =
      ExpectMacro.callsTo[Verification](stubbedMethodCall, mode)(order)
  }

  trait ExpectNoCallsOnMacros {
    transparent inline def on[T <: AnyRef](inline mock: T): Verification =
      ExpectMacro.callsOn[Verification](mock)
  }

  class ExpectNoMoreCallsOnMacros(val ignoringStubs: Boolean) {
    transparent inline def on[T <: AnyRef](inline mock: T): Verification =
      ${ ExpectMacro.callsOnNoMoreImpl[Verification]('mock, '{ this.ignoringStubs }) }
  }

  // Define expect object to return ExpectationOps with macro methods
  object expect {
    def a(callWord: CallWord.type): ExpectationOps       = new ExpectationOps(Times(1))
    def one(callWord: CallWord.type): ExpectationOps     = new ExpectationOps(Times(1))
    def two(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(Times(2))
    def three(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(Times(3))
    def four(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(Times(4))
    def five(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(Times(5))
    def six(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(Times(6))
    def seven(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(Times(7))
    def eight(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(Times(8))
    def nine(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(Times(9))
    def ten(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(Times(10))

    def exactly(calls: Calls): ExpectationOps = new ExpectationOps(Times(calls.times))

    def atLeastOne(callWord: CallWord.type): ExpectationOps     = new ExpectationOps(AtLeast(1))
    def atLeastTwo(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(AtLeast(2))
    def atLeastThree(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(AtLeast(3))
    def atLeastFour(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(AtLeast(4))
    def atLeastFive(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(AtLeast(5))
    def atLeastSix(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(AtLeast(6))
    def atLeastSeven(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(AtLeast(7))
    def atLeastEight(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(AtLeast(8))
    def atLeastNine(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(AtLeast(9))
    def atLeastTen(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(AtLeast(10))

    def atLeast(calls: Calls): ExpectationOps = new ExpectationOps(AtLeast(calls.times))

    def atMostOne(callWord: CallWord.type): ExpectationOps     = new ExpectationOps(AtMost(1))
    def atMostTwo(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(AtMost(2))
    def atMostThree(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(AtMost(3))
    def atMostFour(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(AtMost(4))
    def atMostFive(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(AtMost(5))
    def atMostSix(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(AtMost(6))
    def atMostSeven(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(AtMost(7))
    def atMostEight(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(AtMost(8))
    def atMostNine(callsWord: CallsWord.type): ExpectationOps  = new ExpectationOps(AtMost(9))
    def atMostTen(callsWord: CallsWord.type): ExpectationOps   = new ExpectationOps(AtMost(10))

    def atMost(calls: Calls): ExpectationOps = new ExpectationOps(AtMost(calls.times))

    def no(callsWord: CallsWord.type): ExpectationOps with ExpectNoCallsOnMacros =
      new ExpectationOps(VerifyMacroRuntime.Never) with ExpectNoCallsOnMacros {}

    transparent inline def noMore(inline callsWord: CallsWord.type): ExpectNoMoreCallsOnMacros =
      ${ ExpectMacro.noMoreMacro[ExpectNoMoreCallsOnMacros]('callsWord) }

    def only(callWord: CallWord.type): ExpectationOps = new ExpectationOps(OnlyOn)
  }

  def expect(mode: ScalaVerificationMode): ExpectationOps = new ExpectationOps(mode)

  def InOrder(mocks: AnyRef*)(verifications: VerifyInOrder => Verification): Verification = verifications(VerifyInOrder(mocks))
}
