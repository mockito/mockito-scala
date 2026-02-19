package org.mockito

import scala.util.matching.Regex

/**
 * Constants used by macro implementations. Shared across Scala 2 and Scala 3.
 */
object MacroConstants {

  val MockitoMatchers: Set[String] = Set(
    "anyByte",
    "anyBoolean",
    "anyChar",
    "anyDouble",
    "anyInt",
    "anyFloat",
    "anyShort",
    "anyLong",
    "anyList",
    "anySeq",
    "anyIterable",
    "anySet",
    "anyMap",
    "any",
    "anyVal",
    "$times", // *
    "isNull",
    "isNotNull",
    "eqTo",
    "eqToVal",
    "same",
    "isA",
    "refEq",
    "function0",
    "matches",
    "startsWith",
    "contains",
    "endsWith",
    "argThat",
    "byteThat",
    "booleanThat",
    "charThat",
    "doubleThat",
    "intThat",
    "floatThat",
    "shortThat",
    "longThat",
    "argMatching",
    "$greater",    // >
    "$greater$eq", // >=
    "$less",       // <
    "$less$eq",    // <=
    "$eq$tilde",   // =~
    "Captor.asCapture",
    "capture"
  )

  private val Specs2Implicits: Regex = "(matcher)?[t,T]o(Partial)?FunctionCall(\\d*)".r

  def isSpecs2Matcher(methodName: String): Boolean =
    Specs2Implicits.pattern.matcher(methodName).matches
}
