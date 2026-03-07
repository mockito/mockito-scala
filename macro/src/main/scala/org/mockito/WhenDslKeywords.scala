package org.mockito

/**
 * DSL keyword sets for the idiomatic when/stubbing API. Shared across Scala 2 and Scala 3.
 */
object WhenDslKeywords {
  val ShouldReturnOptions: Set[String]            = Set("shouldReturn", "mustReturn", "returns")
  val FunctionalShouldReturnOptions: Set[String]  = ShouldReturnOptions.map(_ + "F")
  val FunctionalShouldReturnOptions2: Set[String] = ShouldReturnOptions.map(_ + "FG")

  val ShouldCallOptions: Set[String] = Set("shouldCall", "mustCall", "calls")

  val ShouldThrowOptions: Set[String]           = Set("shouldThrow", "mustThrow", "throws")
  val FunctionalShouldFailOptions: Set[String]  = Set("shouldFailWith", "mustFailWith", "failsWith", "raises")
  val FunctionalShouldFailOptions2: Set[String] = Set("shouldFailWithG", "mustFailWithG", "failsWithG", "raisesG")

  val ShouldAnswerOptions: Set[String]            = Set("shouldAnswer", "mustAnswer", "answers")
  val FunctionalShouldAnswerOptions: Set[String]  = ShouldAnswerOptions.map(_ + "F")
  val FunctionalShouldAnswerOptions2: Set[String] = ShouldAnswerOptions.map(_ + "FG")

  val ShouldAnswerPFOptions: Set[String] = Set("shouldAnswerPF", "mustAnswerPF", "answersPF")
}
