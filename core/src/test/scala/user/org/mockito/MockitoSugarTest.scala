package user.org.mockito

import org.mockito.captor.ArgCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.{ CallsRealMethods, DefaultAnswer, ScalaFirstStubbing, ScalaOngoingStubbing }
import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }
import user.org.mockito.matchers.ValueCaseClass

//noinspection RedundantDefaultArgument
class MockitoSugarTest extends WordSpec with MockitoSugar with Matchers with ArgumentMatchersSugar with EitherValues with OptionValues {

  class Foo {
    def bar = "not mocked"

    def iHaveSomeDefaultArguments(noDefault: String, default: String = "default value"): String = ???

    def iStartWithByNameArgs(byName: => String, normal: String): String = ???

    def iHavePrimitiveByNameArgs(byName: => Int, normal: String): String = ???

    def iHaveFunction0Args(normal: String, f0: () => String): String = ???

    def returnBar: Bar = ???

    def doSomethingWithThisIntAndString(v: Int, v2: String): ValueCaseClass = ???

    def returnsValueCaseClass: ValueCaseClass = ???

    def returnsValueCaseClass(i: Int): ValueCaseClass = ???
  }

  class Bar {
    def iAlsoHaveSomeDefaultArguments(noDefault: String, default: String = "default value"): String = ???
  }

  trait Baz {
    def traitMethod(arg: Int): ValueCaseClass = ???
  }

  class HigherKinded[F[_]] {
    def method: F[Either[String, String]] = ???
  }

  class SomeClass extends Foo with Baz

  "mock[T]" should {
    "create a valid mock" in {
      val aMock = mock[Foo]

      when(aMock.bar) thenReturn "mocked!"

      aMock.bar shouldBe "mocked!"
    }

    "stub a value class return value" in {
      val aMock = mock[Foo]

      when(aMock.returnsValueCaseClass) thenReturn ValueCaseClass(100) andThen ValueCaseClass(200)

      aMock.returnsValueCaseClass shouldBe ValueCaseClass(100)
      aMock.returnsValueCaseClass shouldBe ValueCaseClass(200)
    }

    "create a mock with nice answer API (single param)" in {
      val aMock = mock[Baz]

      when(aMock.traitMethod(*)) thenAnswer ((i: Int) => ValueCaseClass(i * 10 + 2)) andThenAnswer ((i: Int) => ValueCaseClass(i * 10 + 3))

      aMock.traitMethod(4) shouldBe ValueCaseClass(42)
      aMock.traitMethod(4) shouldBe ValueCaseClass(43)
    }

    "create a mock with nice answer API (invocation usage)" in {
      val aMock = mock[Baz]

      when(aMock.traitMethod(*)) thenAnswer ((i: InvocationOnMock) => ValueCaseClass(i.getArgument[Int](0) * 10 + 2)) andThenAnswer (
          (i: InvocationOnMock) => ValueCaseClass(i.getArgument[Int](0) * 10 + 3))

      aMock.traitMethod(4) shouldBe ValueCaseClass(42)
      aMock.traitMethod(4) shouldBe ValueCaseClass(43)
    }

    "create a mock with nice answer API (multiple params)" in {
      val aMock = mock[Foo]

      when(aMock.doSomethingWithThisIntAndString(*, *)) thenAnswer ((i: Int, s: String) => ValueCaseClass(i * 10 + s.toInt)) andThenAnswer (
          (i: Int,
           _: String) => ValueCaseClass(i))

      aMock.doSomethingWithThisIntAndString(4, "2") shouldBe ValueCaseClass(42)
      aMock.doSomethingWithThisIntAndString(4, "2") shouldBe ValueCaseClass(4)
    }

    "create a mock while stubbing another" in {
      val aMock = mock[Foo]

      when(aMock.returnBar) thenReturn mock[Bar]

      aMock.returnBar shouldBe a[Bar]
    }

    "default answer should deal with default arguments" in {
      val aMock = mock[Foo]

      aMock.iHaveSomeDefaultArguments("I'm not gonna pass the second argument")
      aMock.iHaveSomeDefaultArguments("I'm gonna pass the second argument", "second argument")

      verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")
      verify(aMock).iHaveSomeDefaultArguments("I'm gonna pass the second argument", "second argument")
    }

    "create a mock with default answer" in {
      val aMock = mock[Foo](CallsRealMethods)

      aMock.bar shouldBe "not mocked"
    }

    "work with higher kinded types and auxiliary methods" in {
      def whenGetById[F[_]](algebra: HigherKinded[F]): ScalaFirstStubbing[F[Either[String, String]]] =
        when(algebra.method)

      val aMock = mock[HigherKinded[Option]]

      whenGetById(aMock) thenReturn Some(Right("Mocked!"))

      aMock.method.value.right.value shouldBe "Mocked!"
    }

    "create a mock with default answer from implicit scope" in {
      implicit val defaultAnswer: DefaultAnswer = CallsRealMethods

      val aMock = mock[Foo]

      aMock.bar shouldBe "not mocked"
    }

    "create a mock with name" in {
      val aMock = mock[Foo]("Nice Mock")

      aMock.toString shouldBe "Nice Mock"
    }

    "work with inline mixins" in {
      val aMock = mock[Foo with Baz]

      when(aMock.bar) thenReturn "mocked!"
      when(aMock.traitMethod(any)) thenReturn ValueCaseClass(69)

      aMock.bar shouldBe "mocked!"
      aMock.traitMethod(30) shouldBe ValueCaseClass(69)

      verify(aMock).traitMethod(30)
    }

    "work with standard mixins" in {
      val aMock = mock[SomeClass]

      when(aMock.bar) thenReturn "mocked!"
      when(aMock.traitMethod(any)) thenReturn ValueCaseClass(69)

      aMock.bar shouldBe "mocked!"
      aMock.traitMethod(30) shouldBe ValueCaseClass(69)

      verify(aMock).traitMethod(30)
    }

    "work with by-name arguments (argument order doesn't matter when not using matchers)" in {
      val aMock = mock[Foo]

      when(aMock.iStartWithByNameArgs("arg1", "arg2")) thenReturn "mocked!"

      aMock.iStartWithByNameArgs("arg1", "arg2") shouldBe "mocked!"
      aMock.iStartWithByNameArgs("arg111", "arg2") shouldBe ""

      verify(aMock).iStartWithByNameArgs("arg1", "arg2")
      verify(aMock).iStartWithByNameArgs("arg111", "arg2")
    }

    "work with primitive by-name arguments" in {
      val aMock = mock[Foo]

      when(aMock.iHavePrimitiveByNameArgs(1, "arg2")) thenReturn "mocked!"

      aMock.iHavePrimitiveByNameArgs(1, "arg2") shouldBe "mocked!"
      aMock.iHavePrimitiveByNameArgs(2, "arg2") shouldBe ""

      verify(aMock).iHavePrimitiveByNameArgs(1, "arg2")
      verify(aMock).iHavePrimitiveByNameArgs(2, "arg2")
    }

    "work with Function0 arguments" in {
      val aMock = mock[Foo]

      when(aMock.iHaveFunction0Args(eqTo("arg1"), function0("arg2"))) thenReturn "mocked!"

      aMock.iHaveFunction0Args("arg1", () => "arg2") shouldBe "mocked!"
      aMock.iHaveFunction0Args("arg1", () => "arg3") shouldBe ""

      verify(aMock).iHaveFunction0Args(eqTo("arg1"), function0("arg2"))
      verify(aMock).iHaveFunction0Args(eqTo("arg1"), function0("arg3"))
    }

    "should stop the user passing traits in the settings" in {
      a[IllegalArgumentException] should be thrownBy {
        mock[Foo](withSettings.extraInterfaces(classOf[Baz]))
      }
    }
  }

  "reset[T]" should {
    "reset mocks" in {
      val aMock = mock[Foo]

      when(aMock.bar) thenReturn "mocked!"
      when(aMock.iHavePrimitiveByNameArgs(1, "arg2")) thenReturn "mocked!"

      aMock.bar shouldBe "mocked!"
      aMock.iHavePrimitiveByNameArgs(1, "arg2") shouldBe "mocked!"

      reset(aMock)

      aMock.bar shouldBe ""
      aMock.iHavePrimitiveByNameArgs(1, "arg2") shouldBe ""

      //to verify the reset mock handler still handles by-name params
      when(aMock.iHavePrimitiveByNameArgs(1, "arg2")) thenReturn "mocked!"

      aMock.iHavePrimitiveByNameArgs(1, "arg2") shouldBe "mocked!"
    }
  }

  "verifyNoMoreInteractions" should {
    "ignore the calls to the methods that provide default arguments" in {
      val aMock = mock[Foo]

      aMock.iHaveSomeDefaultArguments("I'm not gonna pass the second argument")

      verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")
      verifyNoMoreInteractions(aMock)
    }
  }

  "argumentCaptor[T]" should {
    "deal with default arguments" in {
      val aMock = mock[Foo]

      aMock.iHaveSomeDefaultArguments("I'm not gonna pass the second argument")

      val captor1 = ArgCaptor[String]
      val captor2 = ArgCaptor[String]
      verify(aMock).iHaveSomeDefaultArguments(captor1, captor2)

      captor1 hasCaptured "I'm not gonna pass the second argument"
      captor2 hasCaptured "default value"
    }
  }

  "spy[T]" should {
    "create a valid spy" in {
      val aSpy = spy(new Foo)

      when(aSpy.bar) thenReturn "mocked!"

      aSpy.bar shouldBe "mocked!"
    }
  }

  "spyLambda[T]" should {
    "create a valid spy for lambdas and anonymous classes" in {
      val aSpy = spyLambda((arg: String) => s"Got: $arg")

      when(aSpy.apply(any)) thenReturn "mocked!"

      aSpy("hi!") shouldBe "mocked!"
      verify(aSpy).apply("hi!")
    }
  }
}
