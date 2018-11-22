package user.org.mockito

import org.mockito.{ArgumentMatchersSugar, IdiomaticMockito}
import org.scalatest
import org.scalatest.WordSpec
import user.org.mockito.matchers.{ValueCaseClass, ValueClass}

class IdiomaticMockitoTest_212 extends WordSpec with scalatest.Matchers with IdiomaticMockito with ArgumentMatchersSugar {

  class Foo {
    def valueClass(n: Int, v: ValueClass): String = ???

    def valueCaseClass(n: Int, v: ValueCaseClass): String = ???
  }

  "value class matchers" should {

    "eqTo macro works with new syntax" in {
      val aMock = mock[Foo]

      aMock.valueClass(1, eqTo(new ValueClass("meh"))) shouldReturn "mocked!"
      aMock.valueClass(1, new ValueClass("meh")) shouldBe "mocked!"
      aMock.valueClass(1, eqTo(new ValueClass("meh"))) was called

      aMock.valueClass(*, new ValueClass("moo")) shouldReturn "mocked!"
      aMock.valueClass(11, new ValueClass("moo")) shouldBe "mocked!"
      aMock.valueClass(*, new ValueClass("moo")) was called

      val valueClass = new ValueClass("blah")
      aMock.valueClass(1, eqTo(valueClass)) shouldReturn "mocked!"
      aMock.valueClass(1, valueClass) shouldBe "mocked!"
      aMock.valueClass(1, eqTo(valueClass)) was called

      aMock.valueCaseClass(2, eqTo(ValueCaseClass(100))) shouldReturn "mocked!"
      aMock.valueCaseClass(2, ValueCaseClass(100)) shouldBe "mocked!"
      aMock.valueCaseClass(2, eqTo(ValueCaseClass(100))) was called

      val caseClassValue = ValueCaseClass(100)
      aMock.valueCaseClass(3, eqTo(caseClassValue)) shouldReturn "mocked!"
      aMock.valueCaseClass(3, caseClassValue) shouldBe "mocked!"
      aMock.valueCaseClass(3, eqTo(caseClassValue)) was called

      aMock.valueCaseClass(*, ValueCaseClass(200)) shouldReturn "mocked!"
      aMock.valueCaseClass(4, ValueCaseClass(200)) shouldBe "mocked!"
      aMock.valueCaseClass(*, ValueCaseClass(200)) was called
    }
  }
}
