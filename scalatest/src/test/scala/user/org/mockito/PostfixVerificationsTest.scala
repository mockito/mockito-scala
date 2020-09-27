package user.org.mockito

import java.io.{ File, FileOutputStream, ObjectOutputStream }

import org.mockito.captor.ArgCaptor
import org.mockito.exceptions.misusing.NotAMockException
import org.mockito.exceptions.verification._
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito, IdiomaticStubbing, MockitoSugar }
import org.scalactic.Prettifier
import org.scalatest.FixtureContext
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec
import user.org.mockito.matchers.{ ValueCaseClassInt, ValueCaseClassString, ValueClass }
import user.org.mockito.model.JavaFoo

case class Bread(name: String) extends AnyVal
case class Cheese(name: String)

trait IdiomaticMockitoTestSetup extends TableDrivenPropertyChecks { this: IdiomaticStubbing =>
  implicit val prettifier: Prettifier = new Prettifier {
    override def apply(o: Any): String =
      o match {
        case Baz2(_, s) => s"PrettifiedBaz($s)"
        case other      => Prettifier.default(other)
      }
  }

  val scenarios = Table(
    ("testDouble", "orgDouble", "foo"),
    ("mock", () => mock[Org], () => mock[Foo]),
    ("spy", () => spy(new Org), () => spy(new Foo))
  )
}

class PostfixVerificationsTest extends AnyWordSpec with IdiomaticMockitoTestSetup with IdiomaticMockito with Matchers with ArgumentMatchersSugar {

  forAll(scenarios) { (testDouble, orgDouble, foo) =>
    testDouble should {
      "check a mock was not used" in {
        val org = orgDouble()

        org wasNever called
        org wasNever called

        a[NoInteractionsWanted] should be thrownBy {
          org.baz

          org wasNever called
        }
      }

      trait SetupNeverUsed {
        val org = orgDouble()
      }

      "check a mock was not used (with setup)" in new SetupNeverUsed with FixtureContext {
        org wasNever called

        a[NoInteractionsWanted] should be thrownBy {
          org.baz

          org wasNever called
        }
      }

      "check a method was called" in {
        val org = orgDouble()

        org.bar

        org.bar was called

        a[WantedButNotInvoked] should be thrownBy {
          org.baz was called
        }
      }

      "check a method was the only one called" in {
        val org = orgDouble()

        org.bar

        org.bar wasCalled onlyHere

        a[NoInteractionsWanted] should be thrownBy {
          org.baz

          org.baz wasCalled onlyHere
        }
      }

      "check a method wasNever called" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(*, "test") wasNever called

        a[NeverWantedButInvoked] should be thrownBy {
          org.doSomethingWithThisIntAndString(1, "test")

          org.doSomethingWithThisIntAndString(*, "test") wasNever called
        }
      }

      "check a method was called twice" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(1, "test")

        a[TooFewActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled twice
        }
        a[TooFewActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled 2.times
        }

        org.doSomethingWithThisIntAndString(2, "test")

        org.doSomethingWithThisIntAndString(*, "test") wasCalled twice
        org.doSomethingWithThisIntAndString(*, "test") wasCalled 2.times

        org.doSomethingWithThisIntAndString(3, "test")

        a[TooManyActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled twice
        }
        a[TooManyActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled 2.times
        }
      }

      "check a method was called at least twice" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(1, "test")

        a[TooFewActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeastTwice
        }
        a[TooFewActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeast(twice)
        }
        a[TooFewActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeast(2.times)
        }

        org.doSomethingWithThisIntAndString(2, "test")

        org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeastTwice
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeast(twice)
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeast(2.times)
      }

      "check a method was called at most twice" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(1, "test")

        org.doSomethingWithThisIntAndString(*, "test") wasCalled atMostTwice
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atMost(twice)
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atMost(2.times)

        org.doSomethingWithThisIntAndString(2, "test")

        org.doSomethingWithThisIntAndString(*, "test") wasCalled atMostTwice
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atMost(twice)
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atMost(2.times)

        org.doSomethingWithThisIntAndString(3, "test")

        a[MoreThanAllowedActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled atMostTwice
        }
        a[MoreThanAllowedActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled atMost(twice)
        }
        a[MoreThanAllowedActualInvocations] should be thrownBy {
          org.doSomethingWithThisIntAndString(*, "test") wasCalled atMost(2.times)
        }
      }

      "check a mock was not called apart from the verified methods" in {
        val org = orgDouble()

        org.bar

        org.bar was called

        org wasNever calledAgain

        a[NoInteractionsWanted] should be thrownBy {
          org.bar

          org wasNever calledAgain
        }
      }

      "check a mock was not called apart from the verified methods and stubbed" in {
        val org = orgDouble()

        org.baz returns "hola"
        org.baz

        org.bar

        org.bar was called

        org wasNever calledAgain(ignoringStubs)

        a[NoInteractionsWanted] should be thrownBy {
          org.bar

          org wasNever calledAgain(ignoringStubs)
        }
      }

      "work with a captor" in {
        val org       = orgDouble()
        val argCaptor = ArgCaptor[Int]

        org.doSomethingWithThisIntAndString(42, "test")

        org.doSomethingWithThisIntAndString(argCaptor, "test") was called

        argCaptor hasCaptured 42

        an[ArgumentsAreDifferent] should be thrownBy {
          argCaptor hasCaptured 43
        }
      }

      "work with a captor when calling capture explicitly" in {
        val org       = orgDouble()
        val argCaptor = ArgCaptor[Int]

        org.doSomethingWithThisIntAndString(42, "test")

        org.doSomethingWithThisIntAndString(argCaptor.capture, "test") was called

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
            mock2.iHaveDefaultArgs() was called
            mock1.bar was called
          }
        }

        InOrder(mock1, mock2) { implicit order =>
          mock1.bar was called
          mock2.iHaveDefaultArgs() was called
        }
      }

      "work with varargs" in {
        val foo = orgDouble()

        foo.fooWithVarArg("cow", "blue")
        foo.fooWithVarArg("cow", "blue") was called
        foo.fooWithVarArg(*, *) was called

        foo.fooWithVarArg("cat")
        foo.fooWithVarArg("cat") was called

        val s = List("horse", "red")

        foo.fooWithVarArg(s: _*)
        foo.fooWithVarArg("horse", "red") was called

        foo.fooWithVarArg(*, *) wasCalled twice
        foo.fooWithVarArg(*, *, *) wasNever called
      }

      "work with real arrays" in {
        val foo = orgDouble()

        foo.fooWithActualArray(Array("cow", "blue"))
        foo.fooWithActualArray(Array("cow", "blue")) was called

        foo.fooWithActualArray(Array("cow"))
        foo.fooWithActualArray(Array("cow")) was called
      }

      "work with varargs (value class)" in {
        val foo = orgDouble()

        foo.valueClassWithVarArg(Bread("Baguette"), Bread("Arepa"))
        foo.valueClassWithVarArg(Bread("Baguette"), Bread("Arepa")) was called

        foo.valueClassWithVarArg(Bread("Baguette"))
        foo.valueClassWithVarArg(Bread("Baguette")) was called

        val b = Seq(Bread("Chipa"), Bread("Tortilla"))

        foo.valueClassWithVarArg(b: _*)
        foo.valueClassWithVarArg(Bread("Chipa"), Bread("Tortilla")) was called
      }

      "create a mock where I can mix matchers, normal and implicit parameters" in {
        val org                                   = orgDouble()
        implicit val implicitValue: Implicit[Int] = mock[Implicit[Int]]

        org.iHaveTypeParamsAndImplicits[Int, String](*, "test") returns "mocked!"

        org.iHaveTypeParamsAndImplicits(3, "test") shouldBe "mocked!"
        org.iHaveTypeParamsAndImplicits(5, "test") shouldBe "mocked!"
        org.iHaveTypeParamsAndImplicits(5, "est") should not be "mocked!"

        org.iHaveTypeParamsAndImplicits[Int, String](*, "test") wasCalled twice
      }

      "handle the eqTo properly" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(eqTo(1), "meh") returns "mocked!"
        org.doSomethingWithThisIntAndString(1, "meh") shouldBe "mocked!"
        org.doSomethingWithThisIntAndString(1, eqTo("meh")) was called
      }

      "work with multiple param list" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.fooWithSecondParameterList("cow")(cheese)

        org.fooWithSecondParameterList("cow")(cheese) was called
        org.fooWithSecondParameterList("cow")(*) was called
      }

      "work with varargs and multiple param lists" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.fooWithVarArgAndSecondParameterList("cow")(cheese)
        org.fooWithVarArgAndSecondParameterList("cow")(cheese) was called
        org.fooWithVarArgAndSecondParameterList("cow")(*) was called

        org.fooWithVarArgAndSecondParameterList(endsWith("w"))(*) was called
        org.fooWithVarArgAndSecondParameterList(startsWith("c"))(*) was called
        org.fooWithVarArgAndSecondParameterList(contains("ow"))(*) was called
        org.fooWithVarArgAndSecondParameterList(argMatching({ case "cow" => }))(*) was called
        org.fooWithVarArgAndSecondParameterList(argThat((v: String) => v == "cow", "some desc"))(*) was called

        org.fooWithVarArgAndSecondParameterList("cow", "blue")(cheese)
        org.fooWithVarArgAndSecondParameterList("cow", "blue")(cheese) was called
        org.fooWithVarArgAndSecondParameterList(*)(*) wasCalled twice

        val s = List("horse", "red", "meh")
        org.fooWithVarArgAndSecondParameterList(s: _*)(cheese)
        org.fooWithVarArgAndSecondParameterList("horse", "red", "meh")(cheese) was called
        org.fooWithVarArgAndSecondParameterList(*)(*) wasCalled thrice
      }

      "work with actual arrays and multiple param lists" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.fooWithActualArrayAndSecondParameterList(Array("cow"))(cheese)
        org.fooWithActualArrayAndSecondParameterList(Array("cow"))(cheese) was called
        org.fooWithActualArrayAndSecondParameterList(Array("cow"))(*) was called

        org.fooWithActualArrayAndSecondParameterList(Array("cow", "blue"))(cheese)
        org.fooWithActualArrayAndSecondParameterList(Array("cow", "blue"))(cheese) was called
        org.fooWithActualArrayAndSecondParameterList(*)(*) wasCalled twice
      }

      "work with multiple param list (value class)" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.valueClassWithSecondParameterList(Bread("Baguette"))(cheese)

        org.valueClassWithSecondParameterList(Bread("Baguette"))(cheese) was called
        org.valueClassWithSecondParameterList(Bread("Baguette"))(*) was called
      }

      "work with varargs and multiple param lists (value class)" in {
        val org    = orgDouble()
        val cheese = Cheese("Gouda")

        org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"))(cheese)
        org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"))(cheese) was called
        org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"))(*) was called

        org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"), Bread("Arepa"))(cheese)
        org.valueClassWithVarArgAndSecondParameterList(Bread("Baguette"), Bread("Arepa"))(cheese) was called

        val b = Seq(Bread("Chipa"), Bread("Tortilla"))

        org.valueClassWithVarArgAndSecondParameterList(b: _*)(cheese)
        org.valueClassWithVarArgAndSecondParameterList(Bread("Chipa"), Bread("Tortilla"))(cheese) was called
      }

      "eqToVal works with new syntax" in {
        val org = orgDouble()

        org.valueClass(1, eqToVal(new ValueClass("meh"))) returns "mocked!"
        org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
        org.valueClass(1, eqToVal(new ValueClass("meh"))) was called

        org.valueCaseClass(2, eqToVal(ValueCaseClassInt(100))) returns "mocked!"
        org.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
        org.valueCaseClass(2, eqToVal(ValueCaseClassInt(100))) was called

        val caseClassValue = ValueCaseClassInt(100)
        org.valueCaseClass(3, eqToVal(caseClassValue)) returns "mocked!"
        org.valueCaseClass(3, ValueCaseClassInt(100)) shouldBe "mocked!"
        org.valueCaseClass(3, eqToVal(caseClassValue)) was called

        org.valueCaseClass(*, ValueCaseClassInt(200)) returns "mocked!"
        org.valueCaseClass(4, ValueCaseClassInt(200)) shouldBe "mocked!"
        org.valueCaseClass(*, ValueCaseClassInt(200)) was called
      }

      "eqTo macro works with new syntax" in {
        val org = orgDouble()

        org.valueClass(1, eqTo(new ValueClass("meh"))) returns "mocked!"
        org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
        org.valueClass(1, eqTo(new ValueClass("meh"))) was called

        org.valueCaseClass(2, eqTo(ValueCaseClassInt(100))) returns "mocked!"
        org.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
        org.valueCaseClass(2, eqTo(ValueCaseClassInt(100))) was called

        val caseClassValue = ValueCaseClassInt(100)
        org.valueCaseClass(3, eqTo(caseClassValue)) returns "mocked!"
        org.valueCaseClass(3, caseClassValue) shouldBe "mocked!"
        org.valueCaseClass(3, eqTo(caseClassValue)) was called

        org.valueCaseClass(*, ValueCaseClassInt(200)) returns "mocked!"
        org.valueCaseClass(4, ValueCaseClassInt(200)) shouldBe "mocked!"
        org.valueCaseClass(*, ValueCaseClassInt(200)) was called
      }

      "argMatching works with new syntax" in {
        val org = orgDouble()

        org.baz(2, argMatching({ case Baz2(n, _) if n > 90 => })) returns "mocked!"
        org.baz(2, Baz2(100, "pepe")) shouldBe "mocked!"
        org.baz(2, argMatching({ case Baz2(_, "pepe") => })) was called

        an[WantedButNotInvoked] should be thrownBy {
          org.baz(2, argMatching({ case Baz2(99, "pepe") => })) was called
        }
      }

      "anyVal works with new syntax" in {
        val org = orgDouble()

        org.valueClass(1, anyVal[ValueClass]) returns "mocked!"
        org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
        org.valueClass(1, anyVal[ValueClass]) was called

        org.valueCaseClass(2, anyVal[ValueCaseClassInt]) returns "mocked!"
        org.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
        org.valueCaseClass(2, anyVal[ValueCaseClassInt]) was called
      }

      "any works with new syntax" in {
        val org = orgDouble()

        org.valueClass(1, any[ValueClass]) returns "mocked!"
        org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
        org.valueClass(1, any[ValueClass]) was called

        org.valueCaseClass(2, any[ValueCaseClassInt]) returns "mocked!"
        org.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
        org.valueCaseClass(2, any[ValueCaseClassInt]) was called
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
          aMock.baz(42, Baz2(69, "chau")) was called
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

        aMock.iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value") was called
        aMock.iHaveSomeDefaultArguments("I'm gonna pass the second argument", "second argument") was called
      }

      "work with by-name arguments" in {
        val aMock = foo()

        aMock.iStartWithByNameArgs("arg1", "arg2") returns "mocked!"

        aMock.iStartWithByNameArgs("arg1", "arg2") shouldBe "mocked!"
        aMock.iStartWithByNameArgs("arg111", "arg2") should not be "mocked!"

        aMock.iStartWithByNameArgs("arg1", "arg2") was called
        aMock.iStartWithByNameArgs("arg111", "arg2") was called
      }

      "work with primitive by-name arguments" in {
        val aMock = foo()

        aMock.iHavePrimitiveByNameArgs(1, "arg2") returns "mocked!"

        aMock.iHavePrimitiveByNameArgs(1, "arg2") shouldBe "mocked!"
        aMock.iHavePrimitiveByNameArgs(2, "arg2") should not be "mocked!"

        aMock.iHavePrimitiveByNameArgs(1, "arg2") was called
        aMock.iHavePrimitiveByNameArgs(2, "arg2") was called
      }

      "work mixed by-name, normal and vararg arguments" in {
        val aMock = foo()

        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") returns "mocked!"

        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") shouldBe "mocked!"
        aMock.iHaveByNameAndVarArgs("arg2", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") should not be "mocked!"
        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1")("arg5", "arg6", "vararg3", "vararg4") should not be "mocked!"
        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg33", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") should not be "mocked!"

        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") was called
        aMock.iHaveByNameAndVarArgs("arg2", "arg2", "arg3", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") was called
        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg3", "arg4", "vararg1")("arg5", "arg6", "vararg3", "vararg4") was called
        aMock.iHaveByNameAndVarArgs("arg1", "arg2", "arg33", "arg4", "vararg1", "vararg2")("arg5", "arg6", "vararg3", "vararg4") was called
      }

      "work with Function0 arguments" in {
        val aMock = foo()

        aMock.iHaveFunction0Args(eqTo("arg1"), function0("arg2")) returns "mocked!"

        aMock.iHaveFunction0Args("arg1", () => "arg2") shouldBe "mocked!"
        aMock.iHaveFunction0Args("arg1", () => "arg3") should not be "mocked!"

        aMock.iHaveFunction0Args(eqTo("arg1"), function0("arg2")) was called
        aMock.iHaveFunction0Args(eqTo("arg1"), function0("arg3")) was called
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

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) wasCalled twice
      }

      "correctly stub an invocation with matchers and concrete values" in {
        val myService = orgDouble()

        myService.defaultParams("hello", defaultParam2 = *, defaultParam1 = true) returns "hello3true"

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) shouldBe "hello3true"
        myService.defaultParams("hello", defaultParam1 = true, defaultParam2 = 3) shouldBe "hello3true"

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) wasCalled twice
      }

      "correctly stub an invocation dependent values" in {
        val myService = orgDouble()

        myService.curriedDefaultParams("hello", defaultParam1 = true)() returns "hello3true"

        myService.curriedDefaultParams("hello", defaultParam1 = true)() shouldBe "hello3true"

        myService.curriedDefaultParams("hello", defaultParam1 = true)() was called
      }

      "correctly stub an invocation dependent values with default params applied" in {
        val myService = orgDouble()

        myService.curriedDefaultParams("hello")() returns "hello3true"

        myService.curriedDefaultParams("hello")() shouldBe "hello3true"

        myService.curriedDefaultParams("hello")() was called
      }

      "correctly doSomething an invocation with concrete values" in {
        val myService = orgDouble()

        "hello3true" willBe returned by myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true)

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) shouldBe "hello3true"
        myService.defaultParams("hello", defaultParam1 = true, defaultParam2 = 3) shouldBe "hello3true"

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) wasCalled twice
      }

      "correctly do stub an invocation with matchers and concrete values" in {
        val myService = orgDouble()

        "hello3true" willBe returned by myService.defaultParams("hello", defaultParam2 = *, defaultParam1 = true)

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) shouldBe "hello3true"
        myService.defaultParams("hello", defaultParam1 = true, defaultParam2 = 3) shouldBe "hello3true"

        myService.defaultParams("hello", defaultParam2 = 3, defaultParam1 = true) wasCalled twice
      }

      "correctly do stub an invocation dependent values" in {
        val myService = orgDouble()

        "hello3true" willBe returned by myService.curriedDefaultParams("hello", defaultParam1 = true)()

        myService.curriedDefaultParams("hello", defaultParam1 = true)() shouldBe "hello3true"

        myService.curriedDefaultParams("hello", defaultParam1 = true)() was called
      }

      "correctly do stub an invocation dependent values with default params applied" in {
        val myService = orgDouble()

        "hello3true" willBe returned by myService.curriedDefaultParams("hello")()

        myService.curriedDefaultParams("hello")() shouldBe "hello3true"

        myService.curriedDefaultParams("hello")() was called
      }

      "answersPF" in {
        val org = orgDouble()

        org.doSomethingWithThisInt(*) answersPF { case i: Int =>
          i * 10 + 2
        }
        org.doSomethingWithThisIntAndString(*, *) answersPF { case (i: Int, s: String) =>
          (i * 10 + s.toInt).toString
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

      aMock.varargMethod(1, 2, 3) was called
      a[WantedButNotInvoked] should be thrownBy (aMock.varargMethod(1, 2) was called)
    }

    "work when getting varargs from collections" in {
      val aMock = mock[Baz]
      val args  = List(1, 2, 3)

      aMock.varargMethod("hola", args: _*) returns 42

      aMock.varargMethod("hola", 1, 2, 3) shouldBe 42

      aMock.varargMethod("hola", Array(1, 2, 3): _*) was called
      aMock.varargMethod("hola", Vector(1, 2, 3): _*) was called
      aMock.varargMethod("hola", 1, 2, 3) was called
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
      it.next() wasNever called
      result.next() shouldBe 5
      it.next() wasCalled once
    }

    "spies must not be checked for matchers when called for real" in {
      val org        = mock[Org]
      val controller = spy(new TestController(org))

      org.doSomethingWithThisInt(1) returns 1
      // controller is a spy. Calling 'test' for real must not re-evaluate
      // the arguments, hence make a mock call, to register matchers
      controller.test(1)
      org.doSomethingWithThisInt(1) wasCalled once
    }
  }

  val foo: Foo = mock[Foo]

  "mock" should {
    "verify number of interactions with shared mocks appropriately" in {
      foo wasNever called
      foo wasNever calledAgain
    }

    "prevent users using 'calledAgain' on methods" in {
      foo.bar wasNever called

      the[NotAMockException] thrownBy {
        foo.bar wasNever calledAgain
      } should have message
      """|[PostfixVerificationsTest.this.foo.bar] is not a mock!
           |Example of correct verification:
           |    myMock wasNever called
           |""".stripMargin
    }
  }

}
