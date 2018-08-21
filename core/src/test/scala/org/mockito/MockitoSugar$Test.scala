package org.mockito

import org.scalatest.{ Matchers => ScalatestMatchers }
import org.mockito.stubbing.Answer
import org.scalatest.WordSpec

//noinspection RedundantDefaultArgument
class MockitoSugar$Test extends WordSpec with ScalatestMatchers {

  class Foo {
    def bar = "not mocked"

    def iHaveSomeDefaultArguments(noDefault: String, default: String = "default value"): String =
      s"$noDefault - $default"

    def iStartWithByNameArgs(byName: => String, normal: String): String = s"$normal - $byName"

    def iHaveFunction0Args(normal: String, f0: () => String): String = s"$normal - $f0"
  }

  class Bar {
    def iAlsoHaveSomeDefaultArguments(noDefault: String, default: String = "default value"): String =
      s"$noDefault - $default"
  }

  trait Baz {
    def traitMethod(arg: Int): Int = arg + 12
  }

  class SomeClass extends Foo with Baz

  "mock[T]" should {
    "create a valid mock" in {
      val aMock = MockitoSugar.mock[Foo]

      MockitoSugar.when(aMock.bar) thenReturn "mocked!"

      aMock.bar shouldBe "mocked!"
    }

    "create a mock with default answer" in {
      val aMock = MockitoSugar.mock[Foo](Answers.CALLS_REAL_METHODS)

      MockitoSugar
        .mockingDetails(aMock)
        .getMockCreationSettings
        .getDefaultAnswer should be theSameInstanceAs Answers.CALLS_REAL_METHODS
    }

    "create a mock with default answer from implicit scope" in {
      implicit val defaultAnswer: Answer[_] = Answers.CALLS_REAL_METHODS

      val aMock = MockitoSugar.mock[Foo]

      MockitoSugar
        .mockingDetails(aMock)
        .getMockCreationSettings
        .getDefaultAnswer should be theSameInstanceAs Answers.CALLS_REAL_METHODS
    }

    "create a mock with name" in {
      implicit val defaultAnswer: Answer[_] = ScalaDefaultAnswer

      val aMock = MockitoSugar.mock[Foo]("Nice Mock")

      aMock.toString shouldBe "Nice Mock"
    }
  }
}
