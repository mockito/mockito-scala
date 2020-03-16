package org.mockito

import org.mockito.stubbing.{ ScalaFirstStubbing, ScalaOngoingStubbing }
import org.mockito.verification.VerificationMode

import scala.concurrent.duration.Duration

object IdiomaticMockitoBase {
  object Returned
  case class ReturnedBy[T]() {
    def by[S](stubbing: S)(implicit $ev: T <:< S): S = macro DoSomethingMacro.returnedBy[T, S]
  }

  object Answered
  case class AnsweredBy[T]() {
    def by[S](stubbing: S)(implicit $ev: T <:< S): S = macro DoSomethingMacro.answeredBy[T, S]
  }

  object Thrown
  class ThrownBy[E] {
    def by[T](stubbing: T)(implicit $ev: E <:< Throwable): T = macro DoSomethingMacro.thrownBy[T]
  }

  object On
  object Never
  sealed trait CalledAgain
  object IgnoringStubs
  case object CalledAgain extends CalledAgain {
    def apply(i: IgnoringStubs.type): CalledAgain = LenientCalledAgain
  }
  case object LenientCalledAgain extends CalledAgain

  case class Times(times: Int) extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.times(times)
    def within(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).times(times)
    }
    def after(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.after(d.toMillis).times(times)
    }
  }

  //Helper methods for the specs2 macro
  def Exactly(times: Int): Times = Times(times)
  def AtLeastOne: AtLeast        = AtLeast(1)
  def AtLeastTwo: AtLeast        = AtLeast(2)
  def AtLeastThree: AtLeast      = AtLeast(3)
  def AtMostOne: AtMost          = AtMost(1)
  def AtMostTwo: AtMost          = AtMost(2)
  def AtMostThree: AtMost        = AtMost(3)

  case class AtLeast(times: Int) extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.atLeast(times)
    def within(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).atLeast(times)
    }
    def after(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.after(d.toMillis).atLeast(times)
    }
  }

  case class AtMost(times: Int) extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.atMost(times)
    def after(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.after(d.toMillis).atMost(times)
    }
  }

  object OnlyOn extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.only
    def within(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).only
    }
    def after(d: Duration): ScalaVerificationMode = new ScalaVerificationMode {
      override def verificationMode: VerificationMode = Mockito.after(d.toMillis).only
    }
  }

  class ReturnActions[T](os: ScalaFirstStubbing[T]) {
    def apply(value: T, values: T*): ScalaOngoingStubbing[T] = os thenReturn (value, values: _*)
  }

  class ThrowActions[T](os: ScalaFirstStubbing[T]) {
    def apply[E <: Throwable](e: E*): ScalaOngoingStubbing[T] = os thenThrow (e: _*)
  }

  // types for postfix verifications
  object CallWord
  object CallsWord {
    def apply(ignoringStubsWord: IgnoringStubs.type): CallsWord.type = this
  }
}

trait IdiomaticMockitoBase extends IdiomaticStubbing with PostfixVerifications
