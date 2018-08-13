package org.mockito

import org.scalatest
import org.scalatest.WordSpec
import org.mockito.captor.{ Captor => ArgCaptor }

import scala.language.postfixOps

class IdiomaticSyntaxTest
    extends WordSpec
    with MockitoSugar
    with scalatest.Matchers
    with ArgumentMatchersSugar
    with IdiomaticSyntax {

  class Foo {
    def bar = "not mocked"

    def doSomethingWithThisInt(v: Int) = v * 2

    def doSomethingWithThisIntAndString(v: Int, v2: String) = v + v2

  }

  "StubbingOps" should {

    "stub a return value" in {
      val aMock = mock[Foo]

      aMock.bar shouldReturn "mocked!"

      aMock.bar shouldBe "mocked!"
    }

    "stub a real call" in {
      val aMock = mock[Foo]

      aMock.bar shouldCallRealMethod

      aMock.bar shouldBe "not mocked"
    }

    "stub an exception to be thrown" in {
      val aMock = mock[Foo]

      aMock.bar.shouldThrow[IllegalArgumentException]

      an[IllegalArgumentException] shouldBe thrownBy(aMock.bar)
    }

    "stub an exception instance to be thrown" in {
      val aMock = mock[Foo]

      aMock.bar shouldThrow new IllegalArgumentException

      an[IllegalArgumentException] shouldBe thrownBy(aMock.bar)
    }

    "stub an answer" in {
      val aMock = mock[Foo]

      aMock.bar shouldAnswer (_ => "mocked!")

      aMock.bar shouldBe "mocked!"
    }

    "simplify invocation API" in {
      val aMock = mock[Foo]

      aMock.doSomethingWithThisInt(*) shouldAnswer (_.arg0[Int] * 10 + 2)
      aMock.doSomethingWithThisIntAndString(*, *) shouldAnswer (i => (i.arg0[Int] * 10 + i.arg1[String].toInt).toString)

      aMock.doSomethingWithThisInt(4) shouldBe 42
      aMock.doSomethingWithThisIntAndString(4, "2") shouldBe "42"
    }
  }

  "VerificationOps" should {
    "check a mock was not used" in {
      val aMock = mock[Foo]

      aMock wasNotUsed
    }

    "check a method was called" in {
      val aMock = mock[Foo]

      aMock.bar

      aMock.wasCalledOn.bar
      aMock wasCalledOn (_.bar)
    }

    "check a method was the only one called" in {
      val aMock = mock[Foo]

      aMock.bar

      aMock.wasOnlyCalledOn.bar
      aMock wasOnlyCalledOn (_.bar)
    }

    "check a method was never called" in {
      val aMock = mock[Foo]

      aMock.wasNeverCalledOn.bar
      aMock wasNeverCalledOn (_.bar)
    }

    "check a method was called twice" in {
      val aMock = mock[Foo]

      aMock.bar
      aMock.bar

      aMock.wasCalled.twiceOn.bar
      aMock wasCalledOn (_.bar) twice
    }

    "check a method was called many times prefix" in {
      val aMock = mock[Foo]

      (1 to 6).foreach(_ => aMock.bar)

      aMock.wasCalled.sixTimesOn.bar
      aMock wasCalledOn (_.bar) sixTimes
    }

    "check a method was called more times than expected" in {
      val aMock = mock[Foo]

      aMock.bar

      aMock.wasCalledOn.bar

      aMock wasNotUsedAgain
    }

    "work with a captor" in {
      val aMock = mock[Foo]
      val captor = ArgCaptor[Int]

      aMock.doSomethingWithThisInt(42)

      aMock.wasCalledOn.doSomethingWithThisInt(captor)

      captor <-> 42
    }

  }

  "IdiomaticMatchers" should {
    "allow to write '*' instead of any" in {
      val aMock = mock[Foo]

      aMock.doSomethingWithThisInt(*) shouldReturn 42

      aMock.doSomethingWithThisInt(-1) shouldBe 42
    }
  }
}
