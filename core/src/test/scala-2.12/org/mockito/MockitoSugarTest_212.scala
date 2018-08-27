package org.mockito

import org.scalatest
import org.scalatest.WordSpec

//noinspection RedundantDefaultArgument
class MockitoSugarTest_212 extends WordSpec with MockitoSugar with scalatest.Matchers with ArgumentMatchersSugar {

  class Foo {
    def bar = "not mocked"

    def iHaveByNameArgs(normal: String, byName: => String, byName2: => String): String = ???

    def iHaveByNameAndFunction0Args(normal: String, f0: () => String, byName: => String): String = ???
  }

  trait Baz {
    def traitMethod(defaultArg: Int = 30, anotherDefault: String = "hola"): Int = ???
  }

  class SomeClass extends Foo with Baz

  "mock[T]" should {

    "work with default arguments in traits" in {
      val aMock = mock[Foo with Baz]

      when(aMock.bar) thenReturn "mocked!"
      when(aMock.traitMethod(any, any)) thenReturn 69

      aMock.bar shouldBe "mocked!"
      aMock.traitMethod() shouldBe 69

      verify(aMock).traitMethod(30, "hola")
    }

    "work with by-name arguments and matchers (by-name arguments have to be the last ones when using matchers)" in {
      val aMock = mock[Foo]

      when(aMock.iHaveByNameArgs(any, any, any)) thenReturn "mocked!"

      aMock.iHaveByNameArgs("arg1", "arg2", "arg3") shouldBe "mocked!"

      verify(aMock).iHaveByNameArgs(eqTo("arg1"), endsWith("g2"), eqTo("arg3"))
    }

    "work with by-name and Function0 arguments (by-name arguments have to be the last ones when using matchers)" in {
      val aMock = mock[Foo]

      when(aMock.iHaveByNameAndFunction0Args(any, any, any)) thenReturn "mocked!"

      aMock.iHaveByNameAndFunction0Args("arg1", () => "arg2", "arg3") shouldBe "mocked!"

      verify(aMock).iHaveByNameAndFunction0Args(eqTo("arg1"), function0("arg2"), startsWith("arg"))
    }
  }
}
