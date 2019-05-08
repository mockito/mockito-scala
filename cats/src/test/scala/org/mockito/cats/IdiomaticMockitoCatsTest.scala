package org.mockito.cats

import cats.Eq
import cats.implicits._
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }

class IdiomaticMockitoCatsTest
    extends WordSpec
    with Matchers
    with IdiomaticMockito
    with ArgumentMatchersSugar
    with IdiomaticMockitoCats
    with EitherValues
    with OptionValues {

  "mock[T]" should {
    "stub full applicative" in {
      val aMock = mock[Foo]

      aMock.returnsOptionString(*) shouldReturnF "mocked!"

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "stub specific applicative" in {
      val aMock = mock[Foo]

      aMock.returnsOptionT("hello") shouldReturnF "mocked!"

      aMock.returnsOptionT("hello").value shouldBe "mocked!"
    }

    "stub generic applicative" in {
      val aMock = mock[Foo]

      aMock.returnsMT[Option, String]("hello") shouldReturnF "mocked!"

      aMock.returnsMT[Option, String]("hello").value shouldBe "mocked!"
    }

    "work with value classes" in {
      val aMock = mock[Foo]

      aMock.returnsMT[Option, ValueClass](eqTo(ValueClass("hi"))) shouldReturnF ValueClass("mocked!")

      aMock.returnsMT[Option, ValueClass](ValueClass("hi")).value shouldBe ValueClass("mocked!")
    }

    "raise errors" in {
      type ErrorOr[A] = Either[Error, A]
      val aMock = mock[Foo]

      aMock.returnsMT[ErrorOr, ValueClass](eqTo(ValueClass("hi"))) shouldReturnF ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")) shouldFailWith Error("error")

      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi")).right.value shouldBe ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")).left.value shouldBe Error("error")
    }

    "work with cats Eq" in {
      implicit val stringEq: Eq[ValueClass] = Eq.instance((x: ValueClass, y: ValueClass) => x.s.toLowerCase == y.s.toLowerCase)
      val aMock                             = mock[Foo]

      aMock.returnsOptionT(eqTo(ValueClass("HoLa"))) shouldReturnF ValueClass("Mocked!")

      aMock.returnsOptionT(ValueClass("HOLA")) should ===(Some(ValueClass("mocked!")))
    }
  }
}
