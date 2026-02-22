package org.mockito

import org.mockito.stubbing.{ ScalaFirstStubbing, ScalaOngoingStubbing }
import org.mockito.verification.VerificationMode

import scala.concurrent.duration.Duration

/**
 * Runtime support for IdiomaticMockitoBase. Shared across Scala 2 and Scala 3. Macro-based operations are in version-specific IdiomaticMockitoBase. This trait can be extended by
 * objects that need to provide these runtime members (like the scala-2 IdiomaticMockitoBase object).
 */
trait IdiomaticMockitoBaseRuntime {
  // Re-export singleton objects from companion object
  val Returned: IdiomaticMockitoBaseRuntime.Returned.type = IdiomaticMockitoBaseRuntime.Returned
  val Answered: IdiomaticMockitoBaseRuntime.Answered.type = IdiomaticMockitoBaseRuntime.Answered
  val Thrown: IdiomaticMockitoBaseRuntime.Thrown.type     = IdiomaticMockitoBaseRuntime.Thrown
  val On: IdiomaticMockitoBaseRuntime.On.type             = IdiomaticMockitoBaseRuntime.On
  val Never: IdiomaticMockitoBaseRuntime.Never.type       = IdiomaticMockitoBaseRuntime.Never
  type CalledAgain = IdiomaticMockitoBaseRuntime.CalledAgain
  val CalledAgain: IdiomaticMockitoBaseRuntime.CalledAgain.type               = IdiomaticMockitoBaseRuntime.CalledAgain
  val LenientCalledAgain: IdiomaticMockitoBaseRuntime.LenientCalledAgain.type = IdiomaticMockitoBaseRuntime.LenientCalledAgain
  val IgnoringStubs: IdiomaticMockitoBaseRuntime.IgnoringStubs.type           = IdiomaticMockitoBaseRuntime.IgnoringStubs
  type Times = IdiomaticMockitoBaseRuntime.Times
  val Times: IdiomaticMockitoBaseRuntime.Times.type = IdiomaticMockitoBaseRuntime.Times
  type AtLeast = IdiomaticMockitoBaseRuntime.AtLeast
  val AtLeast: IdiomaticMockitoBaseRuntime.AtLeast.type = IdiomaticMockitoBaseRuntime.AtLeast
  type AtMost = IdiomaticMockitoBaseRuntime.AtMost
  val AtMost: IdiomaticMockitoBaseRuntime.AtMost.type = IdiomaticMockitoBaseRuntime.AtMost
  val OnlyOn: IdiomaticMockitoBaseRuntime.OnlyOn.type = IdiomaticMockitoBaseRuntime.OnlyOn
  type ReturnActions[T] = IdiomaticMockitoBaseRuntime.ReturnActions[T]
  type ThrowActions[T]  = IdiomaticMockitoBaseRuntime.ThrowActions[T]
  val CallWord: IdiomaticMockitoBaseRuntime.CallWord.type   = IdiomaticMockitoBaseRuntime.CallWord
  val CallsWord: IdiomaticMockitoBaseRuntime.CallsWord.type = IdiomaticMockitoBaseRuntime.CallsWord
  def Exactly(times: Int): Times                            = IdiomaticMockitoBaseRuntime.Exactly(times)
  val AtLeastOne: AtLeast                                   = IdiomaticMockitoBaseRuntime.AtLeastOne
  val AtLeastTwo: AtLeast                                   = IdiomaticMockitoBaseRuntime.AtLeastTwo
  val AtLeastThree: AtLeast                                 = IdiomaticMockitoBaseRuntime.AtLeastThree
  val AtMostOne: AtMost                                     = IdiomaticMockitoBaseRuntime.AtMostOne
  val AtMostTwo: AtMost                                     = IdiomaticMockitoBaseRuntime.AtMostTwo
  val AtMostThree: AtMost                                   = IdiomaticMockitoBaseRuntime.AtMostThree
}

/**
 * Companion object containing the actual singleton runtime objects and classes.
 */
object IdiomaticMockitoBaseRuntime {
  object Returned
  object Answered
  object Thrown

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
    def within(d: Duration): ScalaVerificationMode  =
      new ScalaVerificationMode {
        override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).times(times)
      }
    def after(d: Duration): ScalaVerificationMode =
      new ScalaVerificationMode {
        override def verificationMode: VerificationMode = Mockito.after(d.toMillis).times(times)
      }
  }

  // Helper methods for the specs2 macro
  def Exactly(times: Int): Times = Times(times)
  def AtLeastOne: AtLeast        = AtLeast(1)
  def AtLeastTwo: AtLeast        = AtLeast(2)
  def AtLeastThree: AtLeast      = AtLeast(3)
  def AtMostOne: AtMost          = AtMost(1)
  def AtMostTwo: AtMost          = AtMost(2)
  def AtMostThree: AtMost        = AtMost(3)

  case class AtLeast(times: Int) extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.atLeast(times)
    def within(d: Duration): ScalaVerificationMode  =
      new ScalaVerificationMode {
        override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).atLeast(times)
      }
    def after(d: Duration): ScalaVerificationMode =
      new ScalaVerificationMode {
        override def verificationMode: VerificationMode = Mockito.after(d.toMillis).atLeast(times)
      }
  }

  case class AtMost(times: Int) extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.atMost(times)
    def after(d: Duration): ScalaVerificationMode   =
      new ScalaVerificationMode {
        override def verificationMode: VerificationMode = Mockito.after(d.toMillis).atMost(times)
      }
  }

  object OnlyOn extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.only
    def within(d: Duration): ScalaVerificationMode  =
      new ScalaVerificationMode {
        override def verificationMode: VerificationMode = Mockito.timeout(d.toMillis).only
      }
    def after(d: Duration): ScalaVerificationMode =
      new ScalaVerificationMode {
        override def verificationMode: VerificationMode = Mockito.after(d.toMillis).only
      }
  }

  class ReturnActions[T](os: ScalaFirstStubbing[T]) {
    def apply(value: T, values: T*): ScalaOngoingStubbing[T] = os.thenReturn(value, values*)
  }

  class ThrowActions[T](os: ScalaFirstStubbing[T]) {
    def apply[E <: Throwable](e: E*): ScalaOngoingStubbing[T] = os thenThrow (e*)
  }

  // types for postfix verifications
  object CallWord
  object CallsWord {
    // No special logic here - the pattern "calls(ignoringStubs)" is detected by Expect macro
    // and transformed into the right Mockito call
    def apply(ignoringStubsWord: IgnoringStubs.type): CallsWord.type = this
  }
}
