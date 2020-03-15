package org.mockito

trait PrefixExpectations extends IdiomaticVerifications {

  import org.mockito.IdiomaticMockitoBase._

  type Calls = Times

  val call: CallWord.type   = CallWord
  val calls: CallsWord.type = CallsWord

  val ignoringStubs: IgnoringStubs.type = IgnoringStubs

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

    def no(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(VerifyMacro.Never)

    def noMore(callsWord: CallsWord.type): ExpectationOps = new ExpectationOps(VerifyMacro.NeverAgain)

    def only(callWord: CallWord.type): ExpectationOps = new ExpectationOps(OnlyOn)
  }
  def expect(mode: ScalaVerificationMode): ExpectationOps = new ExpectationOps(mode)

  class ExpectationOps(val mode: ScalaVerificationMode) {
    def to(invocationOnMock: Any)(implicit order: VerifyOrder): Verification =
      macro ExpectMacro.to[Verification]
  }

  def InOrder(mocks: AnyRef*)(verifications: VerifyInOrder => Verification): Verification = verifications(VerifyInOrder(mocks))

  implicit class IntOps(i: Int) {
    def calls: Calls = Times(i)
    def call: Calls  = Times(i)
  }
}
