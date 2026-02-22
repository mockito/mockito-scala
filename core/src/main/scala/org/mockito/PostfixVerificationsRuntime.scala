package org.mockito

/**
 * Runtime support for PostfixVerifications. Shared across Scala 2 and Scala 3. Macro-based verification operations are in version-specific PostfixVerifications.
 */
trait PostfixVerificationsRuntime extends IdiomaticVerifications {

  import org.mockito.IdiomaticMockitoBaseRuntime.*

  val calledAgain: CalledAgain.type     = CalledAgain
  val ignoringStubs: IgnoringStubs.type = IgnoringStubs

  val on: On.type                = On
  val onlyHere: OnlyOn.type      = OnlyOn
  val once: Times                = Times(1)
  val twice: Times               = Times(2)
  val thrice: Times              = Times(3)
  val threeTimes: Times          = Times(3)
  val fourTimes: Times           = Times(4)
  val fiveTimes: Times           = Times(5)
  val sixTimes: Times            = Times(6)
  val sevenTimes: Times          = Times(7)
  val eightTimes: Times          = Times(8)
  val nineTimes: Times           = Times(9)
  val tenTimes: Times            = Times(10)
  val atLeastOnce: AtLeast       = AtLeast(1)
  val atLeastTwice: AtLeast      = AtLeast(2)
  val atLeastThrice: AtLeast     = AtLeast(3)
  val atLeastThreeTimes: AtLeast = AtLeast(3)
  val atLeastFourTimes: AtLeast  = AtLeast(4)
  val atLeastFiveTimes: AtLeast  = AtLeast(5)
  val atLeastSixTimes: AtLeast   = AtLeast(6)
  val atLeastSevenTimes: AtLeast = AtLeast(7)
  val atLeastEightTimes: AtLeast = AtLeast(8)
  val atLeastNineTimes: AtLeast  = AtLeast(9)
  val atLeastTenTimes: AtLeast   = AtLeast(10)
  val atMostOnce: AtMost         = AtMost(1)
  val atMostTwice: AtMost        = AtMost(2)
  val atMostThrice: AtMost       = AtMost(3)
  val atMostThreeTimes: AtMost   = AtMost(3)
  val atMostFourTimes: AtMost    = AtMost(4)
  val atMostFiveTimes: AtMost    = AtMost(5)
  val atMostSixTimes: AtMost     = AtMost(6)
  val atMostSevenTimes: AtMost   = AtMost(7)
  val atMostEightTimes: AtMost   = AtMost(8)
  val atMostNineTimes: AtMost    = AtMost(9)
  val atMostTenTimes: AtMost     = AtMost(10)

  def atLeast(t: Times): AtLeast = AtLeast(t.times)
  def atMost(t: Times): AtMost   = AtMost(t.times)

  implicit class VerificationsIntOps(i: Int) {
    def times: Times = Times(i)
  }

}
