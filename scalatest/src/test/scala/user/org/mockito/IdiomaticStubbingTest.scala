package user.org.mockito

import java.util.concurrent.atomic.AtomicInteger

import org.mockito.invocation.InvocationOnMock
import org.mockito.{ ArgumentMatchersSugar, IdiomaticStubbing }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import user.org.mockito.matchers.{ ValueCaseClassInt, ValueCaseClassString, ValueClass }
import scala.collection.parallel.immutable
import scala.concurrent.{ Await, Future }
import scala.util.Random

class IdiomaticStubbingTest extends AnyWordSpec with Matchers with ArgumentMatchersSugar with IdiomaticMockitoTestSetup with IdiomaticStubbing {

  forAll(scenarios) { (testDouble, orgDouble, foo) =>
    testDouble should {
      "stub a return value" in {
        val org = orgDouble()

        org.bar returns "mocked!"

        org.bar shouldBe "mocked!"
      }

      "stub a value class return value" in {
        val org = orgDouble()

        org.returnsValueCaseClassInt returns ValueCaseClassInt(100) andThen ValueCaseClassInt(200)

        org.returnsValueCaseClassInt shouldBe ValueCaseClassInt(100)
        org.returnsValueCaseClassInt shouldBe ValueCaseClassInt(200)
      }

      "stub multiple return values" in {
        val org = orgDouble()

        org.bar returns "mocked!" andThen "mocked again!"

        org.bar shouldBe "mocked!"
        org.bar shouldBe "mocked again!"
        org.bar shouldBe "mocked again!"
      }

      "stub an exception instance to be thrown" in {
        val org = orgDouble()

        org.bar throws new IllegalArgumentException

        an[IllegalArgumentException] shouldBe thrownBy(org.bar)
      }

      "chain exception and value" in {
        val org = orgDouble()

        org.bar throws new IllegalArgumentException andThen "mocked!"

        an[IllegalArgumentException] shouldBe thrownBy(org.bar)
        org.bar shouldBe "mocked!"
      }

      "chain value and exception" in {
        val org = orgDouble()

        org.bar returns "mocked!" andThenThrow new IllegalArgumentException

        org.bar shouldBe "mocked!"
        an[IllegalArgumentException] shouldBe thrownBy(org.bar)
      }

      //useful if we want to delay the evaluation of whatever we are returning until the method is called
      "simplify stubbing an answer where we don't care about any param" in {
        val org = orgDouble()

        val counter = new AtomicInteger(1)
        org.bar answers counter.getAndIncrement().toString

        counter.get shouldBe 1
        org.bar shouldBe "1"
        counter.get shouldBe 2
        org.bar shouldBe "2"
      }

      "simplify answer API" in {
        val org = orgDouble()

        org.doSomethingWithThisInt(*) answers ((i: Int) => i * 10 + 2)
        org.doSomethingWithThisIntAndString(*, *) answers ((i: Int, s: String) => (i * 10 + s.toInt).toString)
        org.doSomethingWithThisIntAndStringAndBoolean(*, *, *) answers ((i: Int, s: String, boolean: Boolean) => (i * 10 + s.toInt).toString + boolean)

        org.doSomethingWithThisInt(4) shouldBe 42
        org.doSomethingWithThisIntAndString(4, "2") shouldBe "42"
        org.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42true"
      }

      "create a mock where I can mix matchers and normal parameters (answer)" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndString(*, "test") answers "mocked!"

        org.doSomethingWithThisIntAndString(3, "test") shouldBe "mocked!"
        org.doSomethingWithThisIntAndString(5, "test") shouldBe "mocked!"
        org.doSomethingWithThisIntAndString(5, "est") should not be "mocked!"
      }

      "simplify answer API (invocation usage)" in {
        val org = orgDouble()

        org.doSomethingWithThisInt(*) answers ((i: InvocationOnMock) => i.arg[Int](0) * 10 + 2)

        org.doSomethingWithThisInt(4) shouldBe 42
      }

      "chain answers" in {
        val org = orgDouble()

        org.doSomethingWithThisInt(*) answers ((i: Int) => i * 10 + 2) andThenAnswer ((i: Int) => i * 15 + 9)

        org.doSomethingWithThisInt(4) shouldBe 42
        org.doSomethingWithThisInt(4) shouldBe 69
      }

      "chain answers (invocation usage)" in {
        val org = orgDouble()

        org.doSomethingWithThisInt(*) answers ((i: InvocationOnMock) => i.arg[Int](0) * 10 + 2) andThenAnswer ((i: InvocationOnMock) => i.arg[Int](0) * 15 + 9)

        org.doSomethingWithThisInt(4) shouldBe 42
        org.doSomethingWithThisInt(4) shouldBe 69
      }

      "allow using less params than method on answer stubbing" in {
        val org = orgDouble()

        org.doSomethingWithThisIntAndStringAndBoolean(*, *, *) answers ((i: Int, s: String) => (i * 10 + s.toInt).toString)

        org.doSomethingWithThisIntAndStringAndBoolean(4, "2", v3 = true) shouldBe "42"
      }

      "stub a mock inline that has default args" in {
        val aMock = orgDouble()

        aMock.returnBar returns mock[Bar] andThen mock[Bar]

        aMock.returnBar shouldBe a[Bar]
        aMock.returnBar shouldBe a[Bar]
      }

      "stub a high order function" in {
        val org = orgDouble()

        org.highOrderFunction(*) returns "mocked!"

        org.highOrderFunction(_.toString) shouldBe "mocked!"
      }

      "stub a method that returns a function" in {
        val org = orgDouble()

        org.iReturnAFunction(*).shouldReturn(_.toString).andThen(i => (i * 2).toString).andThenCallRealMethod()

        org.iReturnAFunction(0)(42) shouldBe "42"
        org.iReturnAFunction(0)(42) shouldBe "84"
        org.iReturnAFunction(3)(3) shouldBe "9"
      }

      "doStub a value class return value" in {
        val org = orgDouble()

        ValueCaseClassString("100") willBe returned by org.returnsValueCaseClassString
        ValueCaseClassInt(100) willBe returned by org.returnsValueCaseClassInt

        org.returnsValueCaseClassString shouldBe ValueCaseClassString("100")
        org.returnsValueCaseClassInt shouldBe ValueCaseClassInt(100)
      }

      "doStub a takes value classes" in {
        val org = orgDouble()

        { (v: ValueClass, v1: ValueCaseClassInt, v2: ValueCaseClassString) =>
          s"$v-$v1-$v2"
        } willBe answered by org.takesManyValueClasses(any[ValueClass], any[ValueCaseClassInt], any[ValueCaseClassString])

        org.takesManyValueClasses(new ValueClass("1"), ValueCaseClassInt(2), ValueCaseClassString("3")) shouldBe "ValueClass(1)-ValueCaseClassInt(2)-ValueCaseClassString(3)"
      }

      "doStub return value should be type safe" in {
        val org = orgDouble()

        ValueCaseClassInt(100) willBe returned by org.returnsValueCaseClassInt

        """"mocked" willBe returned by org.returnsValueCaseClass""" shouldNot compile
      }

      "doStub return value should be type safe and allow subtypes" in {
        val org = orgDouble()

        Some("Hola") willBe returned by org.option
        None willBe returned by org.option

        """Some(42) willBe returned by org.option""" shouldNot compile
      }

      "doStub answer value should be type safe" in {
        val org = orgDouble()

        { (v1: Int, _: String) =>
          v1.toString
        } willBe answered by org.doSomethingWithThisIntAndStringAndBoolean(*, *, v3 = true)

        { (_: Int, v2: String) =>
          v2
        } willBe answered by org.doSomethingWithThisIntAndStringAndBoolean(*, *, v3 = true)

        """{ (_: Int, _: String, v3: Boolean) => v3 } willBe answered by org.doSomethingWithThisIntAndStringAndBoolean(*, *, v3 = true)""" shouldNot compile
      }

      "doStub answer function should be type safe and allow subtypes" in {
        val org = orgDouble()

        { (a: String, _: Int) =>
          Some(a)
        } willBe answered by org.option(*, *)

        { (_: String, _: Int) =>
          None: Option[String]
        } willBe answered by org.option(*, *)

        """{ (a: String, b: Int) => Some(b) } willBe answered by org.option2(*, *)""" shouldNot compile
      }

      "doStub a failure" in {
        val org = orgDouble()

        new IllegalArgumentException willBe thrown by org.doSomethingWithThisIntAndStringAndBoolean(*, *, v3 = true)

        org.doSomethingWithThisIntAndStringAndBoolean(1, "2", v3 = false)

        an[IllegalArgumentException] should be thrownBy {
          org.doSomethingWithThisIntAndStringAndBoolean(1, "2", v3 = true)
        }

        """"some value" willBe thrown by org.bar""" shouldNot compile
      }
    }
  }

  "spy" should {
    "stub a function that would fail if the real impl is called" in {
      val aSpy = spy(new Org)

      an[IllegalArgumentException] should be thrownBy {
        aSpy.iBlowUp(*, *) returns "mocked!"
      }

      "mocked!" willBe returned by aSpy.iBlowUp(*, "ok")

      aSpy.iBlowUp(1, "ok") shouldBe "mocked!"
      aSpy.iBlowUp(2, "ok") shouldBe "mocked!"

      an[IllegalArgumentException] should be thrownBy {
        aSpy.iBlowUp(2, "not ok")
      }
    }

    "stub a function with an answer" in {
      val aSpy = spy(new Org)

      ((i: Int) => i * 10 + 2) willBe answered by aSpy.doSomethingWithThisInt(*)
      ((i: Int, s: String) => (i * 10 + s.toInt).toString) willBe answered by aSpy.doSomethingWithThisIntAndString(*, *)
      ((i: Int, s: String, boolean: Boolean) => (i * 10 + s.toInt).toString + boolean) willBe answered by aSpy
        .doSomethingWithThisIntAndStringAndBoolean(*, *, v3 = true)
      val counter = new AtomicInteger(1)
      (() => counter.getAndIncrement().toString) willBe answered by aSpy.bar
      counter.getAndIncrement().toString willBe answered by aSpy.baz

      counter.get shouldBe 1
      aSpy.bar shouldBe "1"
      counter.get shouldBe 2
      aSpy.baz shouldBe "2"
      counter.get shouldBe 3

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

    "stub an object method" in {
      FooObject.simpleMethod shouldBe "not mocked!"

      withObjectSpied[FooObject.type] {
        FooObject.simpleMethod returns "spied!"
        FooObject.simpleMethod shouldBe "spied!"
      }

      FooObject.simpleMethod shouldBe "not mocked!"
    }

    "call real object method when not stubbed" in {
      val now = FooObject.stateDependantMethod
      withObjectSpied[FooObject.type] {
        FooObject.simpleMethod returns s"spied!"
        FooObject.simpleMethod shouldBe s"spied!"
        FooObject.stateDependantMethod shouldBe now
      }
    }

    "be thread safe" when {
      "always stubbing object methods" in {
        immutable.ParSeq.range(1, 100).foreach { i =>
          withObjectSpied[FooObject.type] {
            FooObject.simpleMethod returns s"spied!-$i"
            FooObject.simpleMethod shouldBe s"spied!-$i"
          }
        }
      }

      "intermittently stubbing object methods" in {
        val now = FooObject.stateDependantMethod
        immutable.ParSeq.range(1, 100).foreach { i =>
          if (i % 2 == 0)
            withObjectSpied[FooObject.type] {
              FooObject.stateDependantMethod returns i
              FooObject.stateDependantMethod shouldBe i
            }
          else FooObject.stateDependantMethod shouldBe now
        }
      }
    }
  }

  "mock" should {
    "stub a map" in {
      val mocked = mock[Map[String, String]]
      mocked(*) returns "123"
      mocked("key") shouldBe "123"
    }

    "stub an object method" in {
      FooObject.simpleMethod shouldBe "not mocked!"

      withObjectMocked[FooObject.type] {
        FooObject.simpleMethod returns "mocked!"
        FooObject.simpleMethod shouldBe "mocked!"
      }

      FooObject.simpleMethod shouldBe "not mocked!"
    }

    "object stubbing should be thread safe" in {
      immutable.ParSeq.range(1, 100).foreach { i =>
        withObjectMocked[FooObject.type] {
          FooObject.simpleMethod returns s"mocked!-$i"
          FooObject.simpleMethod shouldBe s"mocked!-$i"
        }
      }
    }

    "object stubbing should be thread safe 2" in {
      val now = FooObject.stateDependantMethod
      immutable.ParSeq.range(1, 100).foreach { i =>
        if (i % 2 == 0)
          withObjectMocked[FooObject.type] {
            FooObject.stateDependantMethod returns i
            FooObject.stateDependantMethod shouldBe i
          }
        else FooObject.stateDependantMethod shouldBe now
      }
    }
  }
}
