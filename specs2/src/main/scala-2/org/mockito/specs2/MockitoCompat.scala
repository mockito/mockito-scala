package org.mockito.specs2

import org.mockito.{ Specs2VerifyMacro, VerifyOrder }
import org.specs2.matcher.MatchResult

/**
 * Scala 2 compatibility layer for Mockito specs2 integration. Provides macro-based verification methods.
 */
private[specs2] trait MockitoCompat extends MockitoSpecs2Support {

  /**
   * class supporting 'was' and 'were' methods to forward mockito calls to the CallsMatcher matcher
   */
  class Calls {
    def were[T](calls: => T)(implicit order: VerifyOrder): Verification = macro Specs2VerifyMacro.wasMacro[T, Verification]
    def was[T](calls: T)(implicit order: VerifyOrder): Verification = macro Specs2VerifyMacro.wasMacro[T, Verification]
  }

  implicit class MatchResultOps[T](m: MatchResult[T]) {
    def andThen[O](calls: => O)(implicit order: VerifyOrder): Verification = macro Specs2VerifyMacro.wasMacro[O, Verification]
  }

  def got[T](calls: => T)(implicit order: VerifyOrder): Verification = macro Specs2VerifyMacro.wasMacro[T, Verification]
}
