package org.mockito.cats

import cats.Eq
import cats.implicits._
import org.mockito.invocation.InvocationOnMock
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Promise }

class IdiomaticMockitoCatsTest
    extends WordSpec
    with Matchers
    with IdiomaticMockito
    with ArgumentMatchersSugar
    with IdiomaticMockitoCats
    with EitherValues
    with OptionValues
    with ScalaFutures {

  "shouldReturn" should {
    "stub full applicative" in {
      val aMock = mock[Foo]

      aMock.returnsOptionString(*) returnsF "mocked!"

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "stub specific applicative" in {
      val aMock = mock[Foo]

      aMock.returnsGenericOption("hello") returnsF "mocked!"

      aMock.returnsGenericOption("hello").value shouldBe "mocked!"
    }

    "stub generic applicative" in {
      val aMock = mock[Foo]

      aMock.returnsMT[Option, String]("hello") returnsF "mocked!"

      aMock.returnsMT[Option, String]("hello").value shouldBe "mocked!"
    }

    "stub composed applicative" in {
      val aMock = mock[Foo]

      aMock.returnsFutureEither("hello") returnsFG ValueClass("mocked!")
      aMock.returnsFutureEither("bye") raisesG Error("boom")

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("bye"))(_.left.value shouldBe Error("boom"))
    }

    "work with value classes" in {
      val aMock = mock[Foo]

      aMock.returnsMT[Option, ValueClass](ValueClass("hi")) returnsF ValueClass("mocked!")

      aMock.returnsMT[Option, ValueClass](ValueClass("hi")).value shouldBe ValueClass("mocked!")
    }

    "create and stub in one line" in {
      val aMock: Foo = mock[Foo].returnsOptionString(*) returnsF "mocked!"

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "raise errors" in {
      val aMock = mock[Foo]

      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi")) returnsF ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")) raises Error("error")

      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi")).right.value shouldBe ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")).left.value shouldBe Error("error")
    }

    "work with cats Eq" in {
      implicit val stringEq: Eq[ValueClass] = Eq.instance((x: ValueClass, y: ValueClass) => x.s.toLowerCase == y.s.toLowerCase)
      val aMock                             = mock[Foo]

      aMock.returnsGenericOption(ValueClass("HoLa")) returnsF ValueClass("Mocked!")
      aMock.shouldI(false) returns "Mocked!"
      aMock.shouldI(true) returns "Mocked again!"

      aMock.returnsGenericOption(ValueClass("HOLA")).value should ===(ValueClass("mocked!"))
      aMock.shouldI(false) shouldBe "Mocked!"
      aMock.shouldI(true) shouldBe "Mocked again!"
    }

    "mix with vanilla api (cats -> vanilla)" in {
      val aMock            = mock[Foo]
      val unrealisedFuture = Promise[ValueClass]()

      aMock.returnsFuture("bye") raises new RuntimeException("Boom") andThen Future.failed(new RuntimeException("Boom2"))
      aMock.returnsFuture("hello") returnsF ValueClass("mocked!") andThen unrealisedFuture.future

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom2")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))

      unrealisedFuture.success(ValueClass("mocked2!"))
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked2!"))
    }

    "work with futures" in {
      val aMock = mock[Foo]

      aMock.returnsFuture("bye") raises new RuntimeException("Boom")
      aMock.returnsFuture("hello") returnsF ValueClass("mocked!")

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))
    }

    "work with EitherT" in {
      val aMock = mock[Foo]

      aMock.returnsEitherT("bye") raises Error("error")
      aMock.returnsEitherT("hello") returnsF ValueClass("mocked!")

      whenReady(aMock.returnsEitherT("bye").value)(_.left.value shouldBe Error("error"))
      whenReady(aMock.returnsEitherT("hello").value)(_.right.value shouldBe ValueClass("mocked!"))
    }

    "work with OptionT" in {
      val aMock = mock[Foo]

      aMock.returnsOptionT("hello") returnsF ValueClass("mocked!")

      aMock.returnsOptionT("hello").value.head.value shouldBe ValueClass("mocked!")
    }
  }

  "shouldAnswer" should {
    "stub single applicative" in {
      val aMock = mock[Foo]

      aMock.returnsOptionString("hello") answersF "mocked!"
      aMock.returnsOptionString("hi") answersF ((s: String) => s + " mocked!")
      aMock.returnsOptionString("hola") answersF ((i: InvocationOnMock) => i.getArgument[String](0) + " invocation mocked!")
      aMock.returnsOptionFrom(42, true) answersF ((i: Int, b: Boolean) => s"$i, $b")

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
      aMock.returnsOptionString("hi").value shouldBe "hi mocked!"
      aMock.returnsOptionString("hola").value shouldBe "hola invocation mocked!"
      aMock.returnsOptionFrom(42, true).value shouldBe "42, true"
    }

    "stub composed applicative" in {
      val aMock = mock[Foo]

      aMock.returnsFutureEither("hello") answersFG ValueClass("mocked!")
      aMock.returnsFutureEither("hi") answersFG ((s: String) => ValueClass(s + " mocked!"))
      aMock.returnsFutureEither("hola") answersFG ((i: InvocationOnMock) => ValueClass(i.getArgument[String](0) + " invocation mocked!"))
      aMock.returnsFutureOptionFrom(42, true) answersFG ((i: Int, b: Boolean) => s"$i, $b")

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("hi"))(_.right.value shouldBe ValueClass("hi mocked!"))
      whenReady(aMock.returnsFutureEither("hola"))(_.right.value shouldBe ValueClass("hola invocation mocked!"))
      whenReady(aMock.returnsFutureOptionFrom(42, true))(_.value shouldBe "42, true")
    }
  }
}
