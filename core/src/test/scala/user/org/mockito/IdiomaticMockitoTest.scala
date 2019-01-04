package user.org.mockito

import org.mockito.captor.ArgCaptor
import org.mockito.exceptions.verification._
import org.mockito.invocation.InvocationOnMock
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.{ Matchers, WordSpec }
import user.org.mockito.matchers.{ ValueCaseClass, ValueClass }

class IdiomaticMockitoTest extends WordSpec with Matchers with IdiomaticMockito with ArgumentMatchersSugar {

  class Implicit[T]

  class Org {
    def bar = "not mocked"
    def baz = "not mocked"

    def doSomethingWithThisInt(v: Int): Int = ???

    def doSomethingWithThisIntAndString(v: Int, v2: String): String = "not mocked"

    def doSomethingWithThisIntAndStringAndBoolean(v: Int, v2: String, v3: Boolean): String = "not mocked"

    def returnBar: Bar = ???

    def highOrderFunction(f: Int => String): String = "not mocked"

    def iReturnAFunction(v: Int): Int => String = i => (i * v).toString

    def iBlowUp(v: Int, v2: String): String = throw new IllegalArgumentException("I was called!")

    def iHaveTypeParamsAndImplicits[A, B](a: A, b: B)(implicit v3: Implicit[A]): String = "not mocked"

    def valueClass(n: Int, v: ValueClass): String = ???

    def valueCaseClass(n: Int, v: ValueCaseClass): String = ???

    def returnsValueCaseClass: ValueCaseClass = ???

    def baz(i: Int, b: Baz): String = ???
  }

  class Bar {
    def iHaveDefaultArgs(v: String = "default"): String = v
  }

  case class Baz(param1: Int, param2: String)

  "StubbingOps" should {
    "stub a return value" in {
      val org = mock[Org]

      org.bar shouldReturn "mocked!"

      org.bar shouldBe "mocked!"
    }

    "stub a value class return value" in {
      val org = mock[Org]

      org.returnsValueCaseClass shouldReturn ValueCaseClass(100) andThen ValueCaseClass(200)

      org.returnsValueCaseClass shouldBe ValueCaseClass(100)
      org.returnsValueCaseClass shouldBe ValueCaseClass(200)
    }

    "stub multiple return values" in {
      val org = mock[Org]

      org.bar shouldReturn "mocked!" andThen "mocked again!"

      org.bar shouldBe "mocked!"
      org.bar shouldBe "mocked again!"
      org.bar shouldBe "mocked again!"
    }

    "stub a real call" in {
      val org = mock[Org]

      org.bar shouldCall realMethod

      org.bar shouldBe "not mocked"
    }

    "stub an exception instance to be thrown" in {
      val org = mock[Org]

      org.bar shouldThrow new IllegalArgumentException

      an[IllegalArgumentException] shouldBe thrownBy(org.bar)
    }

    "chain exception and value" in {
      val org = mock[Org]

      org.bar shouldThrow new IllegalArgumentException andThen "mocked!"

      an[IllegalArgumentException] shouldBe thrownBy(org.bar)
      org.bar shouldBe "mocked!"
    }

    "chain value and exception" in {
      val org = mock[Org]

      org.bar shouldReturn "mocked!" andThenThrow new IllegalArgumentException

      org.bar shouldBe "mocked!"
      an[IllegalArgumentException] shouldBe thrownBy(org.bar)
    }

    //useful if we want to delay the evaluation of whatever we are returning until the method is called
    "simplify stubbing an answer where we don't care about any param" in {
      val org = mock[Org]

      org.bar shouldAnswer "mocked!"

      org.bar shouldBe "mocked!"
    }

    "simplify answer API" in {
      val org = mock[Org]

      org.doSomethingWithThisInt(*) shouldAnswer ((i: Int) => i * 10 + 2)
      org.doSomethingWithThisIntAndString(*, *) shouldAnswer ((i: Int, s: String) => (i * 10 + s.toInt).toString)
      org.doSomethingWithThisIntAndStringAndBoolean(*, *, *) shouldAnswer ((i: Int,
                                                                            s: String,
                                                                            boolean: Boolean) => (i * 10 + s.toInt).toString + boolean)

      org.doSomethingWithThisInt(4) shouldBe 42
      org.doSomethingWithThisIntAndString(4, "2") shouldBe "42"
      org.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42true"
    }

    "create a mock where I can mix matchers and normal parameters (answer)" in {
      val org = mock[Org]

      org.doSomethingWithThisIntAndString(*, "test") shouldAnswer "mocked!"

      org.doSomethingWithThisIntAndString(3, "test") shouldBe "mocked!"
      org.doSomethingWithThisIntAndString(5, "test") shouldBe "mocked!"
      org.doSomethingWithThisIntAndString(5, "est") shouldBe ""
    }

    "simplify answer API (invocation usage)" in {
      val org = mock[Org]

      org.doSomethingWithThisInt(*) shouldAnswer ((i: InvocationOnMock) => i.getArgument[Int](0) * 10 + 2)

      org.doSomethingWithThisInt(4) shouldBe 42
    }

    "chain answers" in {
      val org = mock[Org]

      org.doSomethingWithThisInt(*) shouldAnswer ((i: Int) => i * 10 + 2) andThenAnswer ((i: Int) => i * 15 + 9)

      org.doSomethingWithThisInt(4) shouldBe 42
      org.doSomethingWithThisInt(4) shouldBe 69
    }

    "chain answers (invocation usage)" in {
      val org = mock[Org]

      org.doSomethingWithThisInt(*) shouldAnswer ((i: InvocationOnMock) => i.getArgument[Int](0) * 10 + 2) andThenAnswer (
          (i: InvocationOnMock) => i.getArgument[Int](0) * 15 + 9)

      org.doSomethingWithThisInt(4) shouldBe 42
      org.doSomethingWithThisInt(4) shouldBe 69
    }

    "allow using less params than method on answer stubbing" in {
      val org = mock[Org]

      org.doSomethingWithThisIntAndStringAndBoolean(*, *, *) shouldAnswer ((i: Int, s: String) => (i * 10 + s.toInt).toString)

      org.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42"
    }

    "stub a mock inline that has default args" in {
      val aMock = mock[Org]

      aMock.returnBar shouldReturn mock[Bar] andThen mock[Bar]

      aMock.returnBar shouldBe a[Bar]
      aMock.returnBar shouldBe a[Bar]
    }

    "stub a high order function" in {
      val org = mock[Org]

      org.highOrderFunction(*) shouldReturn "mocked!"

      org.highOrderFunction(_.toString) shouldBe "mocked!"
    }

    "stub a method that returns a function" in {
      val org = mock[Org]

      org.iReturnAFunction(*) shouldReturn (_.toString) andThen (i => (i * 2).toString) andThenCallRealMethod ()

      org.iReturnAFunction(0)(42) shouldBe "42"
      org.iReturnAFunction(0)(42) shouldBe "84"
      org.iReturnAFunction(3)(3) shouldBe "9"
    }
  }

  "DoSomethingOps" should {
    "stub a value class return value" in {
      val org = mock[Org]

      ValueCaseClass(100) willBe returned by org.returnsValueCaseClass

      org.returnsValueCaseClass shouldBe ValueCaseClass(100)
    }

    "stub a spy that would fail if the real impl is called" in {
      val aSpy = spy(new Org)

      an[IllegalArgumentException] should be thrownBy {
        aSpy.iBlowUp(*, *) shouldReturn "mocked!"
      }

      "mocked!" willBe returned by aSpy.iBlowUp(*, "ok")

      aSpy.iBlowUp(1, "ok") shouldBe "mocked!"
      aSpy.iBlowUp(2, "ok") shouldBe "mocked!"

      an[IllegalArgumentException] should be thrownBy {
        aSpy.iBlowUp(2, "not ok")
      }
    }

    "stub a spy with an answer" in {
      val aSpy = spy(new Org)

      ((i: Int) => i * 10 + 2) willBe answered by aSpy.doSomethingWithThisInt(*)
      ((i: Int, s: String) => (i * 10 + s.toInt).toString) willBe answered by aSpy.doSomethingWithThisIntAndString(*, *)
      ((i: Int, s: String, boolean: Boolean) => (i * 10 + s.toInt).toString + boolean) willBe answered by aSpy
        .doSomethingWithThisIntAndStringAndBoolean(*, *, v3 = true)
      (() => "mocked!") willBe answered by aSpy.bar
      "mocked!" willBe answered by aSpy.baz

      aSpy.bar shouldBe "mocked!"
      aSpy.baz shouldBe "mocked!"
      aSpy.doSomethingWithThisInt(4) shouldBe 42
      aSpy.doSomethingWithThisIntAndString(4, "2") shouldBe "42"
      aSpy.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42true"
      aSpy.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = false) shouldBe "not mocked"
    }

    "stub a real call" in {
      val org = mock[Org]

      theRealMethod willBe called by org.doSomethingWithThisIntAndStringAndBoolean(*, *, v3 = true)

      org.doSomethingWithThisIntAndStringAndBoolean(1, "2", v3 = true) shouldBe "not mocked"
      org.doSomethingWithThisIntAndStringAndBoolean(1, "2", v3 = false) shouldBe ""
    }

    "stub a failure" in {
      val org = mock[Org]

      new IllegalArgumentException willBe thrown by org.doSomethingWithThisIntAndStringAndBoolean(*, *, v3 = true)

      org.doSomethingWithThisIntAndStringAndBoolean(1, "2", v3 = false)

      an[IllegalArgumentException] should be thrownBy {
        org.doSomethingWithThisIntAndStringAndBoolean(1, "2", v3 = true)
      }

      """"some value" willBe thrown by org bar""" shouldNot compile
    }
  }

  "VerificationOps" should {

    "check a mock was not used" in {
      val org = mock[Org]

      org wasNever called
      org wasNever called

      a[NoInteractionsWanted] should be thrownBy {
        org.baz

        org wasNever called
      }
    }

    trait SetupNeverUsed {
      val org = mock[Org]
    }

    "check a mock was not used (with setup)" in new SetupNeverUsed {
      org wasNever called

      a[NoInteractionsWanted] should be thrownBy {
        org.baz

        org wasNever called
      }
    }

    "check a method was called" in {
      val org = mock[Org]

      org.bar

      org.bar was called

      a[WantedButNotInvoked] should be thrownBy {
        org.baz was called
      }
    }

    "check a method was the only one called" in {
      val org = mock[Org]

      org.bar

      org.bar wasCalled onlyHere

      a[NoInteractionsWanted] should be thrownBy {
        org.baz

        org.baz wasCalled onlyHere
      }
    }

    "check a method wasNever called" in {
      val org = mock[Org]

      org.doSomethingWithThisIntAndString(*, "test") wasNever called

      a[NeverWantedButInvoked] should be thrownBy {
        org.doSomethingWithThisIntAndString(1, "test")

        org.doSomethingWithThisIntAndString(*, "test") wasNever called
      }
    }

    "check a method was called twice" in {
      val org = mock[Org]

      org.doSomethingWithThisIntAndString(1, "test")

      a[TooLittleActualInvocations] should be thrownBy {
        org.doSomethingWithThisIntAndString(*, "test") wasCalled twice
        org.doSomethingWithThisIntAndString(*, "test") wasCalled 2.times
      }

      org.doSomethingWithThisIntAndString(2, "test")

      org.doSomethingWithThisIntAndString(*, "test") wasCalled twice
      org.doSomethingWithThisIntAndString(*, "test") wasCalled 2.times

      org.doSomethingWithThisIntAndString(3, "test")

      a[TooManyActualInvocations] should be thrownBy {
        org.doSomethingWithThisIntAndString(*, "test") wasCalled twice
        org.doSomethingWithThisIntAndString(*, "test") wasCalled 2.times
      }
    }

    "check a method was called at least twice" in {
      val org = mock[Org]

      org.doSomethingWithThisIntAndString(1, "test")

      a[TooLittleActualInvocations] should be thrownBy {
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeastTwice
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeast(twice)
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeast(2.times)
      }

      org.doSomethingWithThisIntAndString(2, "test")

      org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeastTwice
      org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeast(twice)
      org.doSomethingWithThisIntAndString(*, "test") wasCalled atLeast(2.times)
    }

    "check a method was called at most twice" in {
      val org = mock[Org]

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
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atMost(twice)
        org.doSomethingWithThisIntAndString(*, "test") wasCalled atMost(2.times)
      }
    }

    "check a mock was not called apart from the verified methods" in {
      val org = mock[Org]

      org.bar

      org.bar was called

      org wasNever calledAgain

      a[NoInteractionsWanted] should be thrownBy {
        org.bar

        org wasNever calledAgain
      }
    }

    "work with a captor" in {
      val org       = mock[Org]
      val argCaptor = ArgCaptor[Int]

      org.doSomethingWithThisIntAndString(42, "test")

      org.doSomethingWithThisIntAndString(argCaptor, "test") was called

      argCaptor hasCaptured 42

      an[ArgumentsAreDifferent] should be thrownBy {
        argCaptor hasCaptured 43
      }
    }

    "check invocation order" in {
      val mock1 = mock[Org]
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
      val foo = mock[FooWithVarArg]

      foo.bar("cow", "blue")
      foo.bar("cow", "blue") was called

      foo.bar("cow")
      foo.bar("cow") was called
    }

    "work with varargs (value class)" in {
      val foo = mock[ValueClassWithVarArg]

      foo.bar(Bread("Baguette"), Bread("Arepa"))
      foo.bar(Bread("Baguette"), Bread("Arepa")) was called

      foo.bar(Bread("Baguette"))
      foo.bar(Bread("Baguette")) was called
    }
  }

  "mix arguments and raw parameters" should {
    "create a mock where I can mix matchers, normal and implicit parameters" in {
      val org                                   = mock[Org]
      implicit val implicitValue: Implicit[Int] = mock[Implicit[Int]]

      org.iHaveTypeParamsAndImplicits[Int, String](*, "test") shouldReturn "mocked!"

      org.iHaveTypeParamsAndImplicits(3, "test") shouldBe "mocked!"
      org.iHaveTypeParamsAndImplicits(5, "test") shouldBe "mocked!"
      org.iHaveTypeParamsAndImplicits(5, "est") shouldBe ""

      org.iHaveTypeParamsAndImplicits[Int, String](*, "test") wasCalled twice
    }

    "handle the eqTo properly" in {
      val org = mock[Org]

      org.doSomethingWithThisIntAndString(eqTo(1), "meh") shouldReturn "mocked!"
      org.doSomethingWithThisIntAndString(1, "meh") shouldBe "mocked!"
      org.doSomethingWithThisIntAndString(1, eqTo("meh")) was called
    }

    "work with multiple param list" in {
      val foo = mock[FooWithSecondParameterList]
      val cheese = Cheese("Gouda")

      foo.bar("cow")(cheese)

      foo.bar("cow")(cheese) was called
      foo.bar("cow")(*) was called
    }

    "work with varargs and multiple param lists" in {
      val foo = mock[FooWithVarArgAndSecondParameterList]
      val cheese = Cheese("Gouda")

      foo.bar("cow")(cheese)
      foo.bar("cow")(cheese) was called
      foo.bar("cow")(*) was called

      foo.bar(endsWith("w"))(*) was called
      foo.bar(startsWith("c"))(*) was called
      foo.bar(contains("ow"))(*) was called
      foo.bar(argMatching({ case "cow" => }))(*) was called
      foo.bar(argThat((v: String) => v == "cow", "some desc"))(*) was called

      foo.bar("cow", "blue")(cheese)
      foo.bar("cow", "blue")(cheese) was called
      foo.bar(eqTo("cow", "blue"))(*) was called
      foo.bar(*)(*) wasCalled twice
    }

    "work with multiple param list (value class)" in {
      val foo = mock[ValueClassWithSecondParameterList]
      val cheese = Cheese("Gouda")

      foo.bar(Bread("Baguette"))(cheese)

      foo.bar(Bread("Baguette"))(cheese) was called
      foo.bar(Bread("Baguette"))(*) was called
    }

    "work with varargs and multiple param lists (value class)" in {
      val foo = mock[ValueClassWithVarArgAndSecondParameterList]
      val cheese = Cheese("Gouda")

      foo.bar(Bread("Baguette"))(cheese)
      foo.bar(Bread("Baguette"))(cheese) was called
      foo.bar(Bread("Baguette"))(*) was called

      foo.bar(Bread("Baguette"), Bread("Arepa"))(cheese)
      foo.bar(Bread("Baguette"), Bread("Arepa"))(cheese) was called
      foo.bar(eqTo(Bread("Baguette"), Bread("Arepa")))(*) was called
    }
  }

  "value class matchers" should {
    "eqToVal works with new syntax" in {
      val org = mock[Org]

      org.valueClass(1, eqToVal(new ValueClass("meh"))) shouldReturn "mocked!"
      org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
      org.valueClass(1, eqToVal(new ValueClass("meh"))) was called

      org.valueCaseClass(2, eqToVal(ValueCaseClass(100))) shouldReturn "mocked!"
      org.valueCaseClass(2, ValueCaseClass(100)) shouldBe "mocked!"
      org.valueCaseClass(2, eqToVal(ValueCaseClass(100))) was called

      val caseClassValue = ValueCaseClass(100)
      org.valueCaseClass(3, eqToVal(caseClassValue)) shouldReturn "mocked!"
      org.valueCaseClass(3, ValueCaseClass(100)) shouldBe "mocked!"
      org.valueCaseClass(3, eqToVal(caseClassValue)) was called

      org.valueCaseClass(*, ValueCaseClass(200)) shouldReturn "mocked!"
      org.valueCaseClass(4, ValueCaseClass(200)) shouldBe "mocked!"
      org.valueCaseClass(*, ValueCaseClass(200)) was called
    }

    "eqTo macro works with new syntax" in {
      val org = mock[Org]

      org.valueClass(1, eqTo(new ValueClass("meh"))) shouldReturn "mocked!"
      org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
      org.valueClass(1, eqTo(new ValueClass("meh"))) was called

      org.valueCaseClass(2, eqTo(ValueCaseClass(100))) shouldReturn "mocked!"
      org.valueCaseClass(2, ValueCaseClass(100)) shouldBe "mocked!"
      org.valueCaseClass(2, eqTo(ValueCaseClass(100))) was called

      val caseClassValue = ValueCaseClass(100)
      org.valueCaseClass(3, eqTo(caseClassValue)) shouldReturn "mocked!"
      org.valueCaseClass(3, caseClassValue) shouldBe "mocked!"
      org.valueCaseClass(3, eqTo(caseClassValue)) was called

      org.valueCaseClass(*, ValueCaseClass(200)) shouldReturn "mocked!"
      org.valueCaseClass(4, ValueCaseClass(200)) shouldBe "mocked!"
      org.valueCaseClass(*, ValueCaseClass(200)) was called
    }

    "argMatching works with new syntax" in {
      val org = mock[Org]

      org.baz(2, argMatching({ case Baz(n, _) if n > 90 => })) shouldReturn "mocked!"
      org.baz(2, Baz(100, "pepe")) shouldBe "mocked!"
      org.baz(2, argMatching({ case Baz(_, "pepe") => })) was called

      an[WantedButNotInvoked] should be thrownBy {
        org.baz(2, argMatching({ case Baz(99, "pepe") => })) was called
      }
    }

    "anyVal works with new syntax" in {
      val org = mock[Org]

      org.valueClass(1, anyVal[ValueClass]) shouldReturn "mocked!"
      org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
      org.valueClass(1, anyVal[ValueClass]) was called

      org.valueCaseClass(2, anyVal[ValueCaseClass]) shouldReturn "mocked!"
      org.valueCaseClass(2, ValueCaseClass(100)) shouldBe "mocked!"
      org.valueCaseClass(2, anyVal[ValueCaseClass]) was called
    }

    "any works with new syntax" in {
      val org = mock[Org]

      org.valueClass(1, any[ValueClass]) shouldReturn "mocked!"
      org.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
      org.valueClass(1, any[ValueClass]) was called

      org.valueCaseClass(2, any[ValueCaseClass]) shouldReturn "mocked!"
      org.valueCaseClass(2, ValueCaseClass(100)) shouldBe "mocked!"
      org.valueCaseClass(2, any[ValueCaseClass]) was called
    }
  }
}
