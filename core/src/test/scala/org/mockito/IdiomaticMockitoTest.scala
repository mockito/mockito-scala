package org.mockito

import org.mockito.captor.{ Captor => ArgCaptor }
import org.mockito.exceptions.verification._
import org.scalatest
import org.scalatest.WordSpec

import scala.language.postfixOps

class IdiomaticMockitoTest extends WordSpec with scalatest.Matchers with IdiomaticMockito {

  class Foo {
    def bar = "not mocked"
    def baz = "not mocked"

    def doSomethingWithThisInt(v: Int): Int = v * 2

    def doSomethingWithThisIntAndString(v: Int, v2: String): String = v + v2

    def doSomethingWithThisIntAndStringAndBoolean(v: Int, v2: String, v3: Boolean): String = v + v2 + v3

    def returnBar: Bar = new Bar

    def highOrderFunction(f: Int => String): String = f(42)

    def iReturnAFunction(v: Int): Int => String = i => i * v toString

    def iBlowUp: String = throw new IllegalArgumentException("I was called!")
  }

  class Bar {
    def iHaveDefaultArgs(v: String = "default"): String = v
  }

  "StubbingOps" should {
    "stub a return value" in {
      val aMock = mock[Foo]

      aMock.bar shouldReturn "mocked!"

      aMock.bar shouldBe "mocked!"
    }

    "stub multiple return values" in {
      val aMock = mock[Foo]

      aMock.bar shouldReturn "mocked!" andThen "mocked again!"

      aMock.bar shouldBe "mocked!"
      aMock.bar shouldBe "mocked again!"
      aMock.bar shouldBe "mocked again!"
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

    "chain exception and value" in {
      val aMock = mock[Foo]

      aMock.bar shouldThrow new IllegalArgumentException andThen "mocked!"

      an[IllegalArgumentException] shouldBe thrownBy(aMock.bar)
      aMock.bar shouldBe "mocked!"
    }

    "chain value and exception" in {
      val aMock = mock[Foo]

      aMock.bar shouldReturn "mocked!" andThen new IllegalArgumentException

      aMock.bar shouldBe "mocked!"
      an[IllegalArgumentException] shouldBe thrownBy(aMock.bar)
    }

    //useful if we want to delay the evaluation of whatever we are returning until the method is called
    "simplify stubbing an answer where we don't care about any param" in {
      val aMock = mock[Foo]

      aMock.bar shouldAnswer "mocked!"

      aMock.bar shouldBe "mocked!"
    }

    "simplify answer API" in {
      val aMock = mock[Foo]

      aMock.doSomethingWithThisInt(*) shouldAnswer ((i: Int) => i * 10 + 2)
      aMock.doSomethingWithThisIntAndString(*, *) shouldAnswer ((i: Int, s: String) => i * 10 + s.toInt toString)
      aMock.doSomethingWithThisIntAndStringAndBoolean(*, *, *) shouldAnswer ((i: Int,
                                                                              s: String,
                                                                              boolean: Boolean) =>
                                                                               (i * 10 + s.toInt toString) + boolean)

      aMock.doSomethingWithThisInt(4) shouldBe 42
      aMock.doSomethingWithThisIntAndString(4, "2") shouldBe "42"
      aMock.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42true"
    }

    "chain answers" in {
      val aMock = mock[Foo]

      aMock.doSomethingWithThisInt(*) shouldAnswer ((i: Int) => i * 10 + 2) andThenAnswer ((i: Int) => i * 15 + 9)

      aMock.doSomethingWithThisInt(4) shouldBe 42
      aMock.doSomethingWithThisInt(4) shouldBe 69
    }

    "allow using less params than method on answer stubbing" in {
      val aMock = mock[Foo]

      aMock.doSomethingWithThisIntAndStringAndBoolean(*, *, *) shouldAnswer ((i: Int,
                                                                              s: String) => i * 10 + s.toInt toString)

      aMock.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42"
    }

    "stub a mock inline that has default args" in {
      val aMock = mock[Foo]

      aMock.returnBar shouldReturn mock[Bar] andThen mock[Bar]

      aMock.returnBar shouldBe a[Bar]
      aMock.returnBar shouldBe a[Bar]
    }

    "stub a high order function" in {
      val aMock = mock[Foo]

      aMock.highOrderFunction(*) shouldReturn "mocked!"

      aMock.highOrderFunction(_.toString) shouldBe "mocked!"
    }

    "stub a method that returns a function" in {
      val aMock = mock[Foo]

      aMock.iReturnAFunction(*) shouldReturn (_.toString) andThen (i => (i * 2) toString) andThenCallRealMethod

      aMock.iReturnAFunction(0)(42) shouldBe "42"
      aMock.iReturnAFunction(0)(42) shouldBe "84"
      aMock.iReturnAFunction(3)(3) shouldBe "9"
    }
  }

  "DoSomethingOps" should {
    "stub a spy that would fail if the real impl is called" in {
      val aSpy = spy(new Foo)

      an[IllegalArgumentException] should be thrownBy {
        aSpy.iBlowUp shouldReturn "mocked!"
      }

      "mocked!" willBe returned by aSpy iBlowUp

      aSpy.iBlowUp shouldBe "mocked!"
    }

    "stub a spy with an answer" in {
      val aSpy = spy(new Foo)

      ((i: Int) => i * 10 + 2) willBe answered by aSpy doSomethingWithThisInt *
      ((i: Int, s: String) => i * 10 + s.toInt toString) willBe answered by aSpy doSomethingWithThisIntAndString (*, *)
      ((i: Int, s: String, boolean: Boolean) => (i * 10 + s.toInt toString) + boolean) willBe answered by aSpy doSomethingWithThisIntAndStringAndBoolean (*, *, *)
      ((() => "mocked!") willBe answered by aSpy).bar
      "mocked!" willBe answered by aSpy baz

      aSpy.bar shouldBe "mocked!"
      aSpy.baz shouldBe "mocked!"
      aSpy.doSomethingWithThisInt(4) shouldBe 42
      aSpy.doSomethingWithThisIntAndString(4, "2") shouldBe "42"
      aSpy.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42true"
    }

    "stub a real call" in {
      val aMock = mock[Foo]

      theRealMethod willBe called by aMock bar

      aMock.bar shouldBe "not mocked"
    }

    "stub a failure" in {
      val aMock = mock[Foo]

      new IllegalArgumentException willBe thrown by aMock bar

      an[IllegalArgumentException] should be thrownBy {
        aMock.bar
      }

      """"some value" willBe thrown by aMock bar""" shouldNot compile
    }
  }

  "VerificationOps" should {
    "check a mock was not used" in {
      val aMock = mock[Foo]

      aMock was never called

      a[NoInteractionsWanted] should be thrownBy {
        aMock.baz

        aMock was never called
      }
    }

    "check a method was called" in {
      val aMock = mock[Foo]

      aMock.bar

      aMock wasCalled on bar

      a[WantedButNotInvoked] should be thrownBy {
        aMock wasCalled on baz
      }
    }

    "check a method was the only one called" in {
      val aMock = mock[Foo]

      aMock.bar

      aMock wasCalled onlyOn bar

      a[NoInteractionsWanted] should be thrownBy {
        aMock.baz

        aMock wasCalled onlyOn baz
      }
    }

    "check a method was never called" in {
      val aMock = mock[Foo]

      aMock was never called on bar

      a[NeverWantedButInvoked] should be thrownBy {
        aMock.bar

        aMock was never called on bar
      }
    }

    "check a method was called twice" in {
      val aMock = mock[Foo]

      aMock.bar

      a[TooLittleActualInvocations] should be thrownBy {
        aMock wasCalled twiceOn bar
      }

      aMock.bar

      aMock wasCalled twiceOn bar

      aMock.bar

      a[TooManyActualInvocations] should be thrownBy {
        aMock wasCalled twiceOn bar
      }
    }

    "check a method was called at least twice" in {
      val aMock = mock[Foo]

      aMock.bar

      a[TooLittleActualInvocations] should be thrownBy {
        aMock wasCalled atLeastTwiceOn bar
      }

      aMock.bar

      aMock wasCalled atLeastTwiceOn bar
    }

    "check a method was called at most twice" in {
      val aMock = mock[Foo]

      aMock.bar

      aMock wasCalled atMostTwiceOn bar

      aMock.bar

      aMock wasCalled atMostTwiceOn bar

      aMock.bar

      a[MoreThanAllowedActualInvocations] should be thrownBy {
        aMock wasCalled atMostTwiceOn bar
      }
    }

    "check a method was called more times than expected" in {
      val aMock = mock[Foo]

      aMock.bar

      aMock wasCalled on bar

      aMock was never called again

      a[NoInteractionsWanted] should be thrownBy {
        aMock.bar

        aMock was never called again
      }
    }

    "work with a captor" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[Int]

      aMock.doSomethingWithThisInt(42)

      aMock wasCalled on doSomethingWithThisInt captor

      captor shouldHave 42

      an[AssertionError] should be thrownBy {
        captor shouldHave 43
      }
    }

    "check invocation order" in {
      val mock1 = mock[Foo]
      val mock2 = mock[Bar]

      mock1.bar
      mock2.iHaveDefaultArgs()

      a[VerificationInOrderFailure] should be thrownBy {
        InOrder(mock1, mock2) { implicit order =>
          mock2 wasCalled on iHaveDefaultArgs ()
          mock1 wasCalled on bar
        }
      }
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
