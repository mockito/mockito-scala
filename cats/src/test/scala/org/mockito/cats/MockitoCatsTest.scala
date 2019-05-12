package org.mockito.cats

import cats.Eq
import cats.implicits._
import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }

class MockitoCatsTest
    extends WordSpec
    with Matchers
    with MockitoSugar
    with ArgumentMatchersSugar
    with MockitoCats
    with EitherValues
    with OptionValues {

  "mock[T]" should {
    "stub full applicative" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsOptionString(*)) thenReturn "mocked!"

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "stub specific applicative" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsOptionT("hello")) thenReturn "mocked!"

      aMock.returnsOptionT("hello").value shouldBe "mocked!"
    }

    "stub generic applicative" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsMT[Option, String]("hello")) thenReturn "mocked!"

      aMock.returnsMT[Option, String]("hello").value shouldBe "mocked!"
    }

    "work with value classes" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsMT[Option, ValueClass](eqTo(ValueClass("hi")))) thenReturn ValueClass("mocked!")

      aMock.returnsMT[Option, ValueClass](ValueClass("hi")).value shouldBe ValueClass("mocked!")
    }

    "create and stub in one line" in {
      val aMock: Foo = whenF(mock[Foo].returnsOptionString(*)) thenReturn "mocked!"

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "raise errors" in {
      type ErrorOr[A] = Either[Error, A]
      val aMock = mock[Foo]

      whenF(aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi"))) thenReturn ValueClass("mocked!")
      whenF(aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye"))) thenFailWith Error("error")

      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi")).right.value shouldBe ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")).left.value shouldBe Error("error")
    }

    "work with cats Eq" in {
      implicit val stringEq: Eq[ValueClass] = Eq.instance((x: ValueClass, y: ValueClass) => x.s.toLowerCase == y.s.toLowerCase)
      val aMock                             = mock[Foo]

      whenF(aMock.returnsOptionT(eqTo(ValueClass("HoLa")))) thenReturn ValueClass("Mocked!")

      aMock.returnsOptionT(ValueClass("HOLA")).value should ===(ValueClass("mocked!"))
    }
  }
}
