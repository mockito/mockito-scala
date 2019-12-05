package user.org.mockito

import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }
import user.org.mockito.matchers.{ ValueCaseClassInt, ValueClass }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IdiomaticMockitoTest_212 extends WordSpec with Matchers with IdiomaticMockito with ArgumentMatchersSugar with ScalaFutures {
  class Foo {
    def valueClass(n: Int, v: ValueClass): String = ???

    def valueCaseClass(n: Int, v: ValueCaseClassInt): String = ???
  }

  "value class matchers" should {
    "eqTo macro works with new syntax" in {
      val aMock = mock[Foo]

      aMock.valueClass(1, eqTo(new ValueClass("meh"))) returns "mocked!"
      aMock.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
      aMock.valueClass(1, eqTo(new ValueClass("meh"))) was called

      aMock.valueClass(*, new ValueClass("moo")) returns "mocked!"
      aMock.valueClass(11, new ValueClass("moo")) shouldBe "mocked!"
      aMock.valueClass(*, new ValueClass("moo")) was called

      val valueClass = new ValueClass("blah")
      aMock.valueClass(1, eqTo(valueClass)) returns "mocked!"
      aMock.valueClass(1, valueClass) shouldBe "mocked!"
      aMock.valueClass(1, eqTo(valueClass)) was called

      aMock.valueCaseClass(2, eqTo(ValueCaseClassInt(100))) returns "mocked!"
      aMock.valueCaseClass(2, ValueCaseClassInt(100)) shouldBe "mocked!"
      aMock.valueCaseClass(2, eqTo(ValueCaseClassInt(100))) was called

      val caseClassValue = ValueCaseClassInt(100)
      aMock.valueCaseClass(3, eqTo(caseClassValue)) returns "mocked!"
      aMock.valueCaseClass(3, caseClassValue) shouldBe "mocked!"
      aMock.valueCaseClass(3, eqTo(caseClassValue)) was called

      aMock.valueCaseClass(*, ValueCaseClassInt(200)) returns "mocked!"
      aMock.valueCaseClass(4, ValueCaseClassInt(200)) shouldBe "mocked!"
      aMock.valueCaseClass(*, ValueCaseClassInt(200)) was called
    }

    "work with specialised methods" in {
      val mockFunction = mock[() => Unit]

      val f = Future(mockFunction.apply())

      whenReady(f)(_ => mockFunction() was called)
    }
  }
}
