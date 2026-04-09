package org.mockito

import org.mockito.WhenMacroRuntime.{ AnswerActions, AnswerPFActions, RealMethod }
import org.mockito.stubbing.ScalaOngoingStubbing
import scala.quoted.*
import org.mockito.WhenMacro
import org.mockito.DoSomethingMacro

/**
 * Scala 3 version of IdiomaticStubbing with inline macro-based operations (stubs).
 */
trait IdiomaticStubbing extends IdiomaticStubbingRuntime {
  import org.mockito.IdiomaticMockitoBase.*

  val called: Called.type = Called

  extension [T](inline stubbing: T) {
    transparent inline def shouldReturn: ReturnActions[T]                        = WhenMacro.shouldReturn[T](stubbing).asInstanceOf[ReturnActions[T]]
    transparent inline def mustReturn: ReturnActions[T]                          = WhenMacro.shouldReturn[T](stubbing).asInstanceOf[ReturnActions[T]]
    transparent inline def returns: ReturnActions[T]                             = WhenMacro.shouldReturn[T](stubbing).asInstanceOf[ReturnActions[T]]
    transparent inline infix def shouldReturn(value: T): ScalaOngoingStubbing[T] =
      WhenMacro.shouldReturn[T](stubbing).asInstanceOf[ReturnActions[T]](value)
    transparent inline infix def mustReturn(value: T): ScalaOngoingStubbing[T] =
      WhenMacro.shouldReturn[T](stubbing).asInstanceOf[ReturnActions[T]](value)

    transparent inline def shouldCall(crm: RealMethod.type): ScalaOngoingStubbing[T] = WhenMacro.shouldCallRealMethod[T](stubbing)(using org.scalactic.Prettifier.default)
    transparent inline def mustCall(crm: RealMethod.type): ScalaOngoingStubbing[T]   = WhenMacro.shouldCallRealMethod[T](stubbing)(using org.scalactic.Prettifier.default)
    transparent inline def calls(crm: RealMethod.type): ScalaOngoingStubbing[T]      = WhenMacro.shouldCallRealMethod[T](stubbing)(using org.scalactic.Prettifier.default)

    transparent inline def shouldThrow: ThrowActions[T] = WhenMacro.shouldThrow[T](stubbing).asInstanceOf[ThrowActions[T]]
    transparent inline def mustThrow: ThrowActions[T]   = WhenMacro.shouldThrow[T](stubbing).asInstanceOf[ThrowActions[T]]
    transparent inline def throws: ThrowActions[T]      = WhenMacro.shouldThrow[T](stubbing).asInstanceOf[ThrowActions[T]]

    transparent inline def shouldAnswer: AnswerActions[T] = WhenMacro.shouldAnswer[T](stubbing).asInstanceOf[AnswerActions[T]]
    transparent inline def mustAnswer: AnswerActions[T]   = WhenMacro.shouldAnswer[T](stubbing).asInstanceOf[AnswerActions[T]]
    transparent inline def answers: AnswerActions[T]      = WhenMacro.shouldAnswer[T](stubbing).asInstanceOf[AnswerActions[T]]

    transparent inline def shouldAnswerPF: AnswerPFActions[T] = WhenMacro.shouldAnswerPF[T](stubbing).asInstanceOf[AnswerPFActions[T]]
    transparent inline def mustAnswerPF: AnswerPFActions[T]   = WhenMacro.shouldAnswerPF[T](stubbing).asInstanceOf[AnswerPFActions[T]]
    transparent inline def answersPF: AnswerPFActions[T]      = WhenMacro.shouldAnswerPF[T](stubbing).asInstanceOf[AnswerPFActions[T]]

    transparent inline def isLenient(): Unit = WhenMacro.isLenient[T](stubbing)(using org.scalactic.Prettifier.default)

    transparent inline def shouldDoNothing(): Unit = DoSomethingMacro.doesNothing(stubbing)
    transparent inline def mustDoNothing(): Unit   = DoSomethingMacro.doesNothing(stubbing)
    transparent inline def doesNothing(): Unit     = DoSomethingMacro.doesNothing(stubbing)
  }

  extension [R](v: R) {
    def willBe(r: Returned.type): ReturnedBy[R] = ReturnedBy[R](v)
  }

  extension [R](inline v: R) {
    transparent inline def willBe(a: Answered.type): AnsweredBy[R] = AnsweredBy[R](() => v)
  }

  extension [R](v: () => R) {
    def willBe(a: Answered.type): AnsweredBy[() => R] = AnsweredBy[() => R](() => v)
  }

  extension [P0, R](v: P0 => R) {
    def willBe(a: Answered.type): AnsweredBy[P0 => R] = AnsweredBy[P0 => R](() => v)
  }

  extension [P0, P1, R](v: (P0, P1) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1) => R] = AnsweredBy[(P0, P1) => R](() => v)
  }

  extension [P0, P1, P2, R](v: (P0, P1, P2) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1, P2) => R] = AnsweredBy[(P0, P1, P2) => R](() => v)
  }

  extension [P0, P1, P2, P3, R](v: (P0, P1, P2, P3) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1, P2, P3) => R] = AnsweredBy[(P0, P1, P2, P3) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, R](v: (P0, P1, P2, P3, P4) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1, P2, P3, P4) => R] = AnsweredBy[(P0, P1, P2, P3, P4) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, R](v: (P0, P1, P2, P3, P4, P5) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1, P2, P3, P4, P5) => R] = AnsweredBy[(P0, P1, P2, P3, P4, P5) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, P6, R](v: (P0, P1, P2, P3, P4, P5, P6) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1, P2, P3, P4, P5, P6) => R] = AnsweredBy[(P0, P1, P2, P3, P4, P5, P6) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, P6, P7, R](v: (P0, P1, P2, P3, P4, P5, P6, P7) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1, P2, P3, P4, P5, P6, P7) => R] = AnsweredBy[(P0, P1, P2, P3, P4, P5, P6, P7) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, P6, P7, P8, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1, P2, P3, P4, P5, P6, P7, P8) => R] = AnsweredBy[(P0, P1, P2, P3, P4, P5, P6, P7, P8) => R](() => v)
  }

  extension [P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, R](v: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R) {
    def willBe(a: Answered.type): AnsweredBy[(P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R] = AnsweredBy[(P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R](() => v)
  }

  extension [E](v: E) {
    def willBe(thrown: Thrown.type): ThrownBy[E] = new ThrownBy[E](v)
  }
}
