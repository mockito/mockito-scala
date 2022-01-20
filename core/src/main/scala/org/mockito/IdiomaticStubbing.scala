package org.mockito

import org.mockito.WhenMacro._
import org.mockito.stubbing.ScalaOngoingStubbing

trait IdiomaticStubbing extends MockitoEnhancer with ScalacticSerialisableHack {
  import org.mockito.IdiomaticMockitoBase._

  implicit class StubbingOps[T](stubbing: T) {
    def shouldReturn: ReturnActions[T] = macro WhenMacro.shouldReturn[T]
    def mustReturn: ReturnActions[T] = macro WhenMacro.shouldReturn[T]
    def returns: ReturnActions[T] = macro WhenMacro.shouldReturn[T]

    def shouldCall(crm: RealMethod.type): ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]
    def mustCall(crm: RealMethod.type): ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]
    def calls(crm: RealMethod.type): ScalaOngoingStubbing[T] = macro WhenMacro.shouldCallRealMethod[T]

    def shouldThrow: ThrowActions[T] = macro WhenMacro.shouldThrow[T]
    def mustThrow: ThrowActions[T] = macro WhenMacro.shouldThrow[T]
    def throws: ThrowActions[T] = macro WhenMacro.shouldThrow[T]

    def shouldAnswer: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]
    def mustAnswer: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]
    def answers: AnswerActions[T] = macro WhenMacro.shouldAnswer[T]

    def shouldAnswerPF: AnswerPFActions[T] = macro WhenMacro.shouldAnswerPF[T]
    def mustAnswerPF: AnswerPFActions[T] = macro WhenMacro.shouldAnswerPF[T]
    def answersPF: AnswerPFActions[T] = macro WhenMacro.shouldAnswerPF[T]

    // noinspection AccessorLikeMethodIsUnit
    def isLenient(): Unit = macro WhenMacro.isLenient[T]

    def shouldDoNothing(): Unit = macro DoSomethingMacro.doesNothing
    def mustDoNothing(): Unit = macro DoSomethingMacro.doesNothing
    def doesNothing(): Unit = macro DoSomethingMacro.doesNothing
  }

  val called: Called.type            = Called
  val thrown: Thrown.type            = Thrown
  val returned: Returned.type        = Returned
  val answered: Answered.type        = Answered
  val theRealMethod: RealMethod.type = RealMethod

  val realMethod: RealMethod.type = RealMethod

  implicit class DoSomethingOps[R](v: R) {
    def willBe(r: Returned.type): ReturnedBy[R] = ReturnedBy[R]()
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }

  implicit class DoSomethingOps0[R](v: () => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps1[P0, R](v: P0 => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps2[P0, P1, R](v: (P0, P1) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps3[P0, P1, P2, R](v: (P0, P1, P2) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps4[P0, P1, P2, P3, R](v: (P0, P1, P2, P3) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps5[P0, P1, P2, P3, P4, R](v: (P0, P1, P2, P3, P4) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps6[P0, P1, P2, P3, P4, P5, R](v: (P0, P1, P2, P3, P4, P5) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps7[P0, P1, P2, P3, P4, P5, P6, R](v: (P0, P1, P2, P3, P4, P5, P6) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps8[P0, P1, P2, P3, P4, P5, P6, P7, R](v: (P0, P1, P2, P3, P4, P5, P6, P7) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps9[P0, P1, P2, P3, P4, P5, P6, P7, P8, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }
  implicit class DoSomethingOps10[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R) {
    def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R]()
  }

  implicit class ThrowSomethingOps[E](v: E) {
    def willBe(thrown: Thrown.type): ThrownBy[E] = new ThrownBy[E]
  }
}
