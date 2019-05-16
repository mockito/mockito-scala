package org.mockito.cats

import cats.Eq
import cats.implicits._
import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }

import scala.concurrent.ExecutionContext.Implicits.global

class MockitoCatsTest
    extends WordSpec
    with Matchers
    with MockitoSugar
    with ArgumentMatchersSugar
    with MockitoCats
    with EitherValues
    with OptionValues
    with ScalaFutures {

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

    "stub composed applicative" in {
      val aMock = mock[Foo]

      whenFG(aMock.returnsFutureEither("hello")) thenReturn ValueClass("mocked!")
      whenFG(aMock.returnsFutureEither("bye")) thenFailWith Error("boom")

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("bye"))(_.left.value shouldBe Error("boom"))
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
      when(aMock.shouldI(eqTo(false))) thenReturn "Mocked!"
      when(aMock.shouldI(eqTo(true))) thenReturn "Mocked again!"

      aMock.returnsOptionT(ValueClass("HOLA")).value should ===(ValueClass("mocked!"))
      aMock.shouldI(false) shouldBe "Mocked!"
      aMock.shouldI(true) shouldBe "Mocked again!"
    }
  }
}
