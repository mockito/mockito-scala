package user.org.mockito

import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

import org.mockito.ArgumentMatchersSugar
import org.mockito.IdiomaticMockito
import org.mockito.MockitoSugar
import org.mockito.captor.ArgCaptor
import org.mockito.exceptions.misusing.MissingMethodInvocationException
import org.mockito.exceptions.verification.ArgumentsAreDifferent
import org.mockito.exceptions.verification.MoreThanAllowedActualInvocations
import org.mockito.exceptions.verification.NeverWantedButInvoked
import org.mockito.exceptions.verification.NoInteractionsWanted
import org.mockito.exceptions.verification.TooFewActualInvocations
import org.mockito.exceptions.verification.TooManyActualInvocations
import org.mockito.exceptions.verification.VerificationInOrderFailure
import org.mockito.exceptions.verification.WantedButNotInvoked
import org.scalatest.FixtureContext
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import user.org.mockito.matchers.ValueCaseClassInt
import user.org.mockito.matchers.ValueCaseClassString
import user.org.mockito.matchers.ValueClass
import user.org.mockito.model.JavaFoo

class PrefixExpectationsTest extends AnyWordSpec with Matchers with ArgumentMatchersSugar with IdiomaticMockitoTestSetup with IdiomaticMockito.WithExpect {

  forAll(scenarios) { (testDouble, orgDouble, foo) =>
    testDouble should {
      "check a mock was not used" in {
        val org = orgDouble()

        expect no calls on org
        expect no calls on org

        a[NoInteractionsWanted] should be thrownBy {
          org.baz

          expect no calls on org
        }
      }

      trait SetupNeverUsed {
        val org = orgDouble()
      }

      "check a mock was not used (with setup)" in new SetupNeverUsed with FixtureContext {
        expect no calls on org

        a[NoInteractionsWanted] should be thrownBy {
          org.baz

          expect no calls on org
        }
      }

      val fixture = new SetupNeverUsed {}

      "check a mock was not used (with fixture object)" in {
        expect no calls on fixture.org

        a[NoInteractionsWanted] should be thrownBy {
          fixture.org.baz

          expect no calls on fixture.org
        }
      }

      "prevent misuse of 'expect no calls _to_' instead of 'on' when mock object access looks like a method call" in {
        val exception = intercept[MissingMethodInvocationException] {
          expect no calls to fixture.org
        }

        exception.getMessage shouldBe
        """'expect no calls to <?>' requires an argument which is 'a method call on a mock',
            |  but [fixture.org] is a mock object.
            |
            |The following would be correct (note the usage of 'calls to' vs 'calls on'):
            |    expect no calls to fixture.org.bar(*)
            |    expect no calls on fixture.org
            |""".stripMargin
      }

      "check a method was called once" in {
        val org = orgDouble()

        org.bar

        expect a call to org.bar
        expect one call to org.bar
        expect exactly 1.call to org.bar
        expect(1.call) to org.bar

        a[WantedButNotInvoked] should be thrownBy {
          expect a call to org.baz
        }
        a[WantedButNotInvoked] should be thrownBy {
          expect one call to org.baz
        }
        a[WantedButNotInvoked] should be thrownBy {
          expect exactly 1.call to org.baz
        }
        a[WantedButNotInvoked] should be thrownBy {
          expect(1.call) to org.baz
        }
      }

      "check a method was called once (with setup)" in new SetupNeverUsed with FixtureContext {
        org.bar

        expect a call to org.bar

        a[WantedButNotInvoked] should be thrownBy {
          expect a call to org.baz
        }
      }

      "check a method was the only one called" in {
        val org = orgDouble()

        org.bar

        expect only call to org.bar

        a[NoInteractionsWanted] should be thrownBy {
          org.baz

          expect only call to org.bar
        }
      }

      "check a method was never called" in {
        val org = orgDouble()

        expect no calls to org.doSomethingWithThisIntAndString(*, "test")

        a[NeverWantedButInvoked] should be thrownBy {
          org.doSomethingWithThisIntAndString(1, "test")

          expect no calls to org.doSomethingWithThisIntAndString(*, "test")
        }
      }

      "check a method was called twice" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(1, "test")

        a[TooFewActualInvocations] should be thrownBy {
          expect two calls to org.doSomethingWithThisIntAndString(*, "test")
        }
        a[TooFewActualInvocations] should be thrownBy {
          expect exactly 2.calls to org.doSomethingWithThisIntAndString(*, "test")
        }
        a[TooFewActualInvocations] should be thrownBy {
          expect(2.calls) to org.doSomethingWithThisIntAndString(*, "test")
        }

        org.doSomethingWithThisIntAndString(2, "test")

        expect two calls to org.doSomethingWithThisIntAndString(*, "test")
        expect exactly 2.calls to org.doSomethingWithThisIntAndString(*, "test")
        expect(2.calls) to org.doSomethingWithThisIntAndString(*, "test")

        org.doSomethingWithThisIntAndString(3, "test")

        a[TooManyActualInvocations] should be thrownBy {
          expect two calls to org.doSomethingWithThisIntAndString(*, "test")
        }
        a[TooManyActualInvocations] should be thrownBy {
          expect exactly 2.calls to org.doSomethingWithThisIntAndString(*, "test")
        }
        a[TooManyActualInvocations] should be thrownBy {
          expect(2.calls) to org.doSomethingWithThisIntAndString(*, "test")
        }
      }

      "check a method was called at least twice" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(1, "test")

        a[TooFewActualInvocations] should be thrownBy {
          expect atLeastTwo calls to org.doSomethingWithThisIntAndString(*, "test")
        }
        a[TooFewActualInvocations] should be thrownBy {
          expect atLeast 2.calls to org.doSomethingWithThisIntAndString(*, "test")
        }

        org.doSomethingWithThisIntAndString(2, "test")

        expect atLeastTwo calls to org.doSomethingWithThisIntAndString(*, "test")
        expect atLeast 2.calls to org.doSomethingWithThisIntAndString(*, "test")
      }

      "check a method was called at most twice" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(1, "test")

        expect atMostTwo calls to org.doSomethingWithThisIntAndString(*, "test")
        expect atMost 2.calls to org.doSomethingWithThisIntAndString(*, "test")

        org.doSomethingWithThisIntAndString(2, "test")

        expect atMostTwo calls to org.doSomethingWithThisIntAndString(*, "test")
        expect atMost 2.calls to org.doSomethingWithThisIntAndString(*, "test")

        org.doSomethingWithThisIntAndString(3, "test")

        a[MoreThanAllowedActualInvocations] should be thrownBy {
          expect atMostTwo calls to org.doSomethingWithThisIntAndString(*, "test")
        }
        a[MoreThanAllowedActualInvocations] should be thrownBy {
          expect atMost 2.calls to org.doSomethingWithThisIntAndString(*, "test")
        }
      }

      "check a mock was not called apart from the verified methods" in {
        val org = orgDouble()

        org.bar

        expect a call to org.bar

        expect noMore calls on org

        a[NoInteractionsWanted] should be thrownBy {
          org.bar

          expect noMore calls on org
        }
      }

      "check a mock was not called apart from the verified methods and stubbed" in {
        val org = orgDouble()

        org.baz returns "hola"
        org.baz

        org.bar

        expect a call to org.bar

        expect noMore calls(ignoringStubs) on org

        a[NoInteractionsWanted] should be thrownBy {
          org.bar

          expect noMore calls(ignoringStubs) on org
        }
      }

      "work with a captor" in {
        val org       = orgDouble()
        val argCaptor = ArgCaptor[Int]

        org.doSomethingWithThisIntAndString(42, "test")

        expect a call to org.doSomethingWithThisIntAndString(argCaptor, "test")

        argCaptor hasCaptured 42

        an[ArgumentsAreDifferent] should be thrownBy {
          argCaptor hasCaptured 43
        }
      }

      "work with a captor when calling capture explicitly" in {
        val org       = orgDouble()
        val argCaptor = ArgCaptor[Int]

        org.doSomethingWithThisIntAndString(42, "test")

        expect a call to org.doSomethingWithThisIntAndString(argCaptor.capture, "test")

        argCaptor hasCaptured 42

        an[ArgumentsAreDifferent] should be thrownBy {
          argCaptor hasCaptured 43
        }
      }

      "check invocation order" in {
        val mock1 = orgDouble()
        val mock2 = mock[Bar]

        mock1.bar
        mock2.iHaveDefaultArgs()

        a[VerificationInOrderFailure] should be thrownBy {
          InOrder(mock1, mock2) { implicit order =>
            expect a call to mock2.iHaveDefaultArgs()
            expect a call to mock1.bar
          }
        }

        InOrder(mock1, mock2) { implicit order =>
          expect a call to mock1.bar
          expect a call to mock2.iHaveDefaultArgs()
        }
      }

      "work with varargs" in {
        val foo = orgDouble()

        foo.fooWithVarArg("cow", "blue")
        expect a call to foo.fooWithVarArg("cow", "blue")
        expect a call to foo.fooWithVarArg(*, *)

        foo.fooWithVarArg("cat")
        expect a call to foo.fooWithVarArg("cat")

        val s = List("horse", "red")

        foo.fooWithVarArg(s: _*)
        expect a call to foo.fooWithVarArg("horse", "red")

        expect two calls to foo.fooWithVarArg(*, *)
        expect no calls to foo.fooWithVarArg(*, *, *)
      }

      "work with real arrays" in {
        val foo = orgDouble()

        foo.fooWithActualArray(Array("cow", "blue"))
        expect a call to foo.fooWithActualArray(Array("cow", "blue"))

        foo.fooWithActualArray(Array("cow"))
        expect a call to foo.fooWithActualArray(Array("cow"))
      }

      "work with varargs (value class)" in {
        val foo = orgDouble()

        foo.valueClassWithVarArg(Bread("Baguette"), Bread("Arepa"))
        expect a call to foo.valueClassWithVarArg(Bread("Baguette"), Bread("Arepa"))

        foo.valueClassWithVarArg(Bread("Baguette"))
        expect a call to foo.valueClassWithVarArg(Bread("Baguette"))

        val b = Seq(Bread("Chipa"), Bread("Tortilla"))

        foo.valueClassWithVarArg(b: _*)
        expect a call to foo.valueClassWithVarArg(Bread("Chipa"), Bread("Tortilla"))
      }

      "create a mock where I can mix matchers, normal and implicit parameters" in {
        val org                                   = orgDouble()
        implicit val implicitValue: Implicit[Int] = mock[Implicit[Int]]

        org.iHaveTypeParamsAndImplicits[Int, String](*, "test") returns "mocked!"

        org.iHaveTypeParamsAndImplicits(3, "test") shouldBe "mocked!"
        org.iHaveTypeParamsAndImplicits(5, "test") shouldBe "mocked!"
        org.iHaveTypeParamsAndImplicits(5, "est") should not be "mocked!"

        expect two calls to org.iHaveTypeParamsAndImplicits[Int, String](*, "test")
      }

      "handle the eqTo properly" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(eqTo(1), "meh") returns "mocked!"
        org.doSomethingWithThisIntAndString(1, "meh") shouldBe "mocked!"
        expect a call to org.doSomethingWithThisIntAndString(1, eqTo("meh"))
      }

      "work with multiple param list" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.fooWithSecondParameterList("cow")(cheese)

        expect a call to org.fooWithSecondParameterList("cow")(cheese)
        expect a call to org.fooWithSecondParameterList("cow")(*)
      }

      "work with varargs and multiple param lists" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.fooWithVarArgAndSecondParameterList("cow")(cheese)
        expect a call to org.fooWithVarArgAndSecondParameterList("cow")(cheese)
        expect a call to org.fooWithVarArgAndSecondParameterList("cow")(*)

        expect a call to org.fooWithVarArgAndSecondParameterList(endsWith("w"))(*)
        expect a call to org.fooWithVarArgAndSecondParameterList(startsWith("c"))(*)
        expect a call to org.fooWithVarArgAndSecondParameterList(contains("ow"))(*)
        expect a call to org.fooWithVarArgAndSecondParameterList(argMatching({ case "cow" => }))(*)
        expect a call to org.fooWithVarArgAndSecondParameterList(argThat((v: String) => v == "cow", "some desc"))(*)

        org.fooWithVarArgAndSecondParameterList("cow", "blue")(cheese)
        expect a call to org.fooWithVarArgAndSecondParameterList("cow", "blue")(cheese)
        expect two calls to org.fooWithVarArgAndSecondParameterList(*)(*)

        val s = List("horse", "red", "meh")
        org.fooWithVarArgAndSecondParameterList(s: _*)(cheese)
        expect a call to org.fooWithVarArgAndSecondParameterList("horse", "red", "meh")(cheese)
        expect three calls to org.fooWithVarArgAndSecondParameterList(*)(*)
      }

      "work with actual arrays and multiple param lists" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.fooWithActualArrayAndSecondParameterList(Array("cow"))(cheese)
        expect a call to org.fooWithActualArrayAndSecondParameterList(Array("cow"))(cheese)
        expect a call to org.fooWithActualArrayAndSecondParameterList(Array("cow"))(*)

        org.fooWithActualArrayAndSecondParameterList(Array("cow", "blue"))(cheese)
        expect a call to org.fooWithActualArrayAndSecondParameterList(Array("cow", "blue"))(cheese)
        expect two calls to org.fooWithActualArrayAndSecondParameterList(*)(*)
      }

      "work with multiple param list (value class)" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.valueClassWithSecondParameterList(Bread("Baguette"))(cheese)

        expect a call to org.valueClassWithSecondParameterList(Bread("Baguette"))(cheese)
        expect a call to org.valueClassWithSecondParameterList(Bread("Baguette"))(*)
      }

      "work with varargs and multiple param lists (value class)" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"))(cheese)
        expect a call to org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"))(cheese)
        expect a call to org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"))(*)

        org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"), Bread("Arepa"))(cheese)
        expect a call to org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"), Bread("Arepa"))(cheese)

        val b = Seq(Bread("Chipa"), Bread("Tortilla"))

        org.valueClassWithVarArgAndSecondParameterList(b: _*)(cheese)
        expect a call to org.valueClassWithVarArgAndSecondParameterList(Bread("Chipa"), Bread("Tortilla"))(cheese)
      }

      "eqToVal works with new syntax" in {
        val org = orgDouble()

        org.valueClass(1, eqToVal(new ValueClass("meh"))) returns "mocked!"
        org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
        expect a call to org.valueClass(1, eqToVal(new ValueClass("meh")))

        org.valueCaseClass(2, eqToVal(ValueCaseClassInt(100))) returns "mocked!"
        org.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
        expect a call to org.valueCaseClass(2, eqToVal(ValueCaseClassInt(100)))

        val caseClassValue = ValueCaseClassInt(100)
        org.valueCaseClass(3, eqToVal(caseClassValue)) returns "mocked!"
        org.valueCaseClass(3, ValueCaseClassInt(100)) shouldBe "mocked!"
        expect a call to org.valueCaseClass(3, eqToVal(caseClassValue))

        org.valueCaseClass(*, ValueCaseClassInt(200)) returns "mocked!"
        org.valueCaseClass(4, ValueCaseClassInt(200)) shouldBe "mocked!"
        expect a call to org.valueCaseClass(*, ValueCaseClassInt(200))
      }

      "eqTo macro works with new syntax" in {
        val org = orgDouble()

        org.valueClass(1, eqTo(new ValueClass("meh"))) returns "mocked!"
        org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
        expect a call to org.valueClass(1, eqTo(new ValueClass("meh")))

        org.valueCaseClass(2, eqTo(ValueCaseClassInt(100))) returns "mocked!"
        org.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
        expect a call to org.valueCaseClass(2, eqTo(ValueCaseClassInt(100)))

        val caseClassValue = ValueCaseClassInt(100)
        org.valueCaseClass(3, eqTo(caseClassValue)) returns "mocked!"
        org.valueCaseClass(3, caseClassValue) shouldBe "mocked!"
        expect a call to org.valueCaseClass(3, eqTo(caseClassValue))

        org.valueCaseClass(*, ValueCaseClassInt(200)) returns "mocked!"
        org.valueCaseClass(4, ValueCaseClassInt(200)) shouldBe "mocked!"
        expect a call to org.valueCaseClass(*, ValueCaseClassInt(200))
      }

      "argMatching works with new syntax" in {
        val org = orgDouble()

        org.baz(2, argMatching({ case Baz2(n, _) if n > 90 => })) returns "mocked!"
        org.baz(2, Baz2(100, "pepe")) shouldBe "mocked!"
        expect a call to org.baz(2, argMatching({ case Baz2(_, "pepe") => }))

        an[WantedButNotInvoked] should be thrownBy {
          expect a call to org.baz(2, argMatching({ case Baz2(99, "pepe") => }))
        }
      }

      "anyVal works with new syntax" in {
        val org = orgDouble()

        org.valueClass(1, anyVal[ValueClass]) returns "mocked!"
        org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
        expect a call to org.valueClass(1, anyVal[ValueClass])

        org.valueCaseClass(2, anyVal[ValueCaseClassInt]) returns "mocked!"
        org.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
        expect a call to org.valueCaseClass(2, anyVal[ValueCaseClassInt])
      }

      "any works with new syntax" in {
        val org = orgDouble()

        org.valueClass(1, any[ValueClass]) returns "mocked!"
        org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
        expect a call to org.valueClass(1, any[ValueClass])

        org.valueCaseClass(2, any[ValueCaseClassInt]) returns "mocked!"
        org.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
        expect a call to org.valueCaseClass(2, any[ValueCaseClassInt])
      }

      "works with arg value classes" in {
        val org = orgDouble()

        org.takesManyValueClasses(any[ValueClass], any[ValueCaseClassInt], any[ValueCaseClassString]) answers { (v: ValueClass, v1: ValueCaseClassInt, v2: ValueCaseClassString) =>
          s"$v-$v1-$v2"
        }

        org.takesManyValueClasses(new ValueClass("1"), ValueCaseClassInt(2), ValueCaseClassString("3")) shouldBe "ValueClass(1)-ValueCaseClassInt(2)-ValueCaseClassString(3)"
      }

      "works with tagged value classes" in {
        val org = orgDouble()

        org.printTaggedValue(any[TaggedValue[String]]) returns "hello"

        org.printTaggedValue(TaggedValue[String](1)) shouldBe "hello"
      }

      "use Prettifier for the arguments" in {
        val aMock = orgDouble()

        aMock.baz(42, Baz2(69, "hola"))

        val e = the[ArgumentsAreDifferent] thrownBy {
          expect a call to aMock.baz(42, Baz2(69, "chau"))
        }

        e.getMessage should include("Argument(s) are different! Wanted:")
        e.getMessage should include("org.baz(42, PrettifiedBaz(hola));")
        e.getMessage should include("Actual invocations have different arguments:")
        e.getMessage should include("org.baz(42, PrettifiedBaz(chau));")
      }

      "default answer should deal with default arguments" in {
        val aMock = foo()

        aMock.iHaveSomeDefaultArguments("I'm not gonna pass the second argument")
        aMock.iHaveSomeDefaultArguments("I'm gonna pass the second argument", "second argument")

        expect a call to aMock.iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")
        expect a call to aMock.iHaveSomeDefaultArguments("I'm gonna pass the second argument", "second argument")
      }

      "work with by-name arguments" in {
        val aMock = foo()

        aMock.iStartWithByNameArgs("arg1", "arg2") returns "mocked!"

        aMock.iStartWithByNameArgs("arg1", "arg2") shouldBe "mocked!"
        aMock.iStartWithByNameArgs("arg111", "arg2") should not be "mocked!"

        expect a call to aMock.iStartWithByNameArgs("arg1", "arg2")
        expect a call to aMock.iStartWithByNameArgs("arg111", "arg2")
      }

      "work with primitive by-name arguments" in {
        val aMock = foo()

        aMock.iHavePrimitiveByNameArgs(1, "arg2") returns "mocked!"

        aMock.iHavePrimitiveByNameArgs(1, "arg2") shouldBe "mocked!"
        aMock.iHavePrimitiveByNameArgs(2, "arg2") should not be "mocked!"

        expect a call to aMock.iHavePrimitiveByNameArgs(1, "arg2")
        expect a call to aMock.iHavePrimitiveByNameArgs(2, "arg2")
      }

      "work mixed by-name, normal and vararg arguments" in {
        val aMock = foo()

        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") returns "mocked!"

        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") shouldBe "mocked!"
        aMock.iHaveByNameAndVarArgs("arg2", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") should not be "mocked!"
        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1")("arg5", "arg6", "vararg3", "vararg4") should not be "mocked!"
        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg33", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") should not be "mocked!"

        expect a call to aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4")
        expect a call to aMock.iHaveByNameAndVarArgs("arg2", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4")
        expect a call to aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1")("arg5", "arg6", "vararg3", "vararg4")
        expect a call to aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg33", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4")
      }

      "work with Function0 arguments" in {
        val aMock = foo()

        aMock.iHaveFunction0Args(eqTo("arg1"), function0("arg2")) returns "mocked!"

        aMock.iHaveFunction0Args("arg1", () => "arg2") shouldBe "mocked!"
        aMock.iHaveFunction0Args("arg1", () => "arg3") should not be "mocked!"

        expect a call to aMock.iHaveFunction0Args(eqTo("arg1"), function0("arg2"))
        expect a call to aMock.iHaveFunction0Args(eqTo("arg1"), function0("arg3"))
      }

      "reset" in {
        val aMock = foo()

        aMock.bar returns "mocked!"
        aMock.iHavePrimitiveByNameArgs(1, "arg2") returns "mocked!"

        aMock.bar shouldBe "mocked!"
        aMock.iHavePrimitiveByNameArgs(1, "arg2") shouldBe "mocked!"

        MockitoSugar.reset(aMock)

        aMock.bar should not be "mocked!"
        aMock.iHavePrimitiveByNameArgs(1, "arg2") should not be "mocked!"

        //to verify the reset mock handler still handles by-name params
        aMock.iHavePrimitiveByNameArgs(1, "arg2") returns "mocked!"

        aMock.iHavePrimitiveByNameArgs(1, "arg2") shouldBe "mocked!"
      }

      "correctly stub an invocation with concrete values" in {
        val myService = orgDouble()

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) returns "hello3true"

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) shouldBe "hello3true"
        myService.defaultParams("hello", defaultParam1 = true, defaultParam2 = 3) shouldBe "hello3true"

        expect two calls to myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true)
      }

      "correctly stub an invocation with matchers and concrete values" in {
        val myService = orgDouble()

        myService.defaultParams("hello", defaultParam2 = *, defaultParam1 = true) returns "hello3true"

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) shouldBe "hello3true"
        myService.defaultParams("hello", defaultParam1 = true, defaultParam2 = 3) shouldBe "hello3true"

        expect two calls to myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true)
      }

      "correctly stub an invocation dependent values" in {
        val myService = orgDouble()

        myService.curriedDefaultParams("hello", defaultParam1 = true)() returns "hello3true"

        myService.curriedDefaultParams("hello", defaultParam1 = true)() shouldBe "hello3true"

        expect a call to myService.curriedDefaultParams("hello", defaultParam1 = true)()
      }

      "correctly stub an invocation dependent values with default params applied" in {
        val myService = orgDouble()

        myService.curriedDefaultParams("hello")() returns "hello3true"

        myService.curriedDefaultParams("hello")() shouldBe "hello3true"

        expect a call to myService.curriedDefaultParams("hello")()
      }

      "correctly doSomething an invocation with concrete values" in {
        val myService = orgDouble()

        "hello3true" willBe returned by myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true)

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) shouldBe "hello3true"
        myService.defaultParams("hello", defaultParam1 = true, defaultParam2 = 3) shouldBe "hello3true"

        expect two calls to myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true)
      }

      "correctly do stub an invocation with matchers and concrete values" in {
        val myService = orgDouble()

        "hello3true" willBe returned by myService.defaultParams("hello", defaultParam2 = *, defaultParam1 = true)

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) shouldBe "hello3true"
        myService.defaultParams("hello", defaultParam1 = true, defaultParam2 = 3) shouldBe "hello3true"

        expect two calls to myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true)
      }

      "correctly do stub an invocation dependent values" in {
        val myService = orgDouble()

        "hello3true" willBe returned by myService.curriedDefaultParams("hello", defaultParam1 = true)()

        myService.curriedDefaultParams("hello", defaultParam1 = true)() shouldBe "hello3true"

        expect a call to myService.curriedDefaultParams("hello", defaultParam1 = true)()
      }

      "correctly do stub an invocation dependent values with default params applied" in {
        val myService = orgDouble()

        "hello3true" willBe returned by myService.curriedDefaultParams("hello")()

        myService.curriedDefaultParams("hello")() shouldBe "hello3true"

        expect a call to myService.curriedDefaultParams("hello")()
      }

      "answersPF" in {
        val org = orgDouble()

        org.doSomethingWithThisInt(*) answersPF {
          case i: Int => i * 10 + 2
        }
        org.doSomethingWithThisIntAndString(*, *) answersPF {
          case (i: Int, s: String) => (i * 10 + s.toInt).toString
        }
        org.doSomethingWithThisIntAndStringAndBoolean(*, *, *) answersPF {
          case (i: Int, s: String, true)  => (i * 10 + s.toInt).toString + " verdadero"
          case (i: Int, s: String, false) => (i * 10 + s.toInt).toString + " falso"
        }

        org.doSomethingWithThisInt(4) shouldBe 42
        org.doSomethingWithThisIntAndString(4, "2") shouldBe "42"
        org.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42 verdadero"
        org.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = false) shouldBe "42 falso"
      }
    }
  }

  "mock" should {

    "stub a no op call" in {
      val org = mock[Org]

      org.unit().doesNothing()

      org.unit() shouldBe ()
    }

    "stub a real call" in {
      val org: Org = mock[Org].bar shouldCall realMethod
      org.bar shouldBe "not mocked"
    }

    "be serialisable" in {
      val list = mock[java.util.List[String]](withSettings.name("list1").serializable())
      list.get(3) returns "mocked"
      list.get(3) shouldBe "mocked"

      val file = File.createTempFile("mock", "tmp")
      file.deleteOnExit()

      val oos = new ObjectOutputStream(new FileOutputStream(file))
      oos.writeObject(list)
      oos.close()
    }

    "work with java varargs" in {
      val aMock = mock[JavaFoo]

      aMock.varargMethod(1, 2, 3) returns 42

      aMock.varargMethod(1, 2, 3) shouldBe 42

      expect a call to aMock.varargMethod(1, 2, 3)
      a[WantedButNotInvoked] should be thrownBy {
        expect a call to aMock.varargMethod(1, 2)
      }
    }

    "work when getting varargs from collections" in {
      val aMock = mock[Baz]
      val args  = List(1, 2, 3)

      aMock.varargMethod("hola", args: _*) returns 42

      aMock.varargMethod("hola", 1, 2, 3) shouldBe 42

      expect a call to aMock.varargMethod("hola", Array(1, 2, 3): _*)
      expect a call to aMock.varargMethod("hola", Vector(1, 2, 3): _*)
      expect a call to aMock.varargMethod("hola", 1, 2, 3)
    }

    "return the same value class on a function" in {
      val f: String => ValueClass = mock[String => ValueClass]

      f(*) returns new ValueClass("str")

      f("anyStringValue") shouldEqual new ValueClass("str")
    }
  }

  "spy" should {
    "interact correctly with the real object" in {
      val it     = spy(Iterator.continually("hello"))
      val result = it.map(_.length)
      expect no calls to it.next()
      result.next() shouldBe 5
      expect one call to it.next()
    }

    "spies must not be checked for matchers when called for real" in {
      val org        = mock[Org]
      val controller = spy(new TestController(org))

      org.doSomethingWithThisInt(1) returns 1
      // controller is a spy. Calling 'test' for real must not re-evaluate
      // the arguments, hence make a mock call, to register matchers
      controller.test(1)
      expect one call to org.doSomethingWithThisInt(1)
    }
  }

}
