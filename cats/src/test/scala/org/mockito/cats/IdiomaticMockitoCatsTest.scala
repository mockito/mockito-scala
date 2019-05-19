package org.mockito.cats

import cats.Eq
import cats.implicits._
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

    "stub composed applicative" in {
      val aMock = mock[Foo]

      aMock.returnsFutureEither("hello") shouldReturnFG ValueClass("mocked!")
      aMock.returnsFutureEither("bye") shouldFailWithG Error("boom")

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("bye"))(_.left.value shouldBe Error("boom"))
    }

    "work with value classes" in {
      val aMock = mock[Foo]

      aMock.returnsMT[Option, ValueClass](ValueClass("hi")) shouldReturnF ValueClass("mocked!")

      aMock.returnsMT[Option, ValueClass](ValueClass("hi")).value shouldBe ValueClass("mocked!")
    }

    "create and stub in one line" in {
      val aMock: Foo = mock[Foo].returnsOptionString(*) shouldReturnF "mocked!"

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "raise errors" in {
      val aMock = mock[Foo]

      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi")) shouldReturnF ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")) shouldFailWith Error("error")

      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi")).right.value shouldBe ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")).left.value shouldBe Error("error")
    }

    "work with cats Eq" in {
      implicit val stringEq: Eq[ValueClass] = Eq.instance((x: ValueClass, y: ValueClass) => x.s.toLowerCase == y.s.toLowerCase)
      val aMock                             = mock[Foo]

      aMock.returnsOptionT(ValueClass("HoLa")) shouldReturnF ValueClass("Mocked!")
      aMock.shouldI(false) shouldReturn "Mocked!"
      aMock.shouldI(true) shouldReturn "Mocked again!"

      aMock.returnsOptionT(ValueClass("HOLA")).value should ===(ValueClass("mocked!"))
      aMock.shouldI(false) shouldBe "Mocked!"
      aMock.shouldI(true) shouldBe "Mocked again!"
    }

    "mix with vanilla api (cats -> vanilla)" in {
      val aMock            = mock[Foo]
      val unrealisedFuture = Promise[ValueClass]()

      aMock.returnsFuture("bye") shouldFailWith new RuntimeException("Boom") andThen Future.failed(
        new RuntimeException("Boom2"))
      aMock.returnsFuture("hello") shouldReturnF ValueClass("mocked!") andThen unrealisedFuture.future

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom2")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))

      unrealisedFuture.success(ValueClass("mocked2!"))
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked2!"))
    }

    "work with futures" in {
      val aMock            = mock[Foo]

      aMock.returnsFuture("bye") shouldFailWith new RuntimeException("Boom")
      aMock.returnsFuture("hello") shouldReturnF ValueClass("mocked!")

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))
    }
  }

  "willReturn" should {
    "stub full applicative" in {
      val aMock = mock[Foo]

      "mocked!" willBe returnedF by aMock.returnsOptionString(*)

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "stub specific applicative" in {
      val aMock = mock[Foo]

      "mocked!" willBe returnedF by aMock.returnsOptionT("hello")

      aMock.returnsOptionT("hello").value shouldBe "mocked!"
    }

    "stub generic applicative" in {
      val aMock = mock[Foo]

      "mocked!" willBe returnedF by aMock.returnsMT[Option, String]("hello")

      aMock.returnsMT[Option, String]("hello").value shouldBe "mocked!"
    }

    "stub composed applicative" in {
      val aMock = mock[Foo]

      ValueClass("mocked!") willBe returnedFG by aMock.returnsFutureEither("hello")
      Error("boom") willBe raisedG by aMock.returnsFutureEither("bye")

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("bye"))(_.left.value shouldBe Error("boom"))
    }

    "work with value classes" in {
      val aMock = mock[Foo]

      ValueClass("mocked!") willBe returnedF by aMock.returnsMT[Option, ValueClass](ValueClass("hi"))

      aMock.returnsMT[Option, ValueClass](ValueClass("hi")).value shouldBe ValueClass("mocked!")
    }

    "raise errors" in {
      val aMock = mock[Foo]

      ValueClass("mocked!") willBe returnedF by aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi"))
      Error("error") willBe raised by aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye"))

      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi")).right.value shouldBe ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")).left.value shouldBe Error("error")
    }

    "work with futures" in {
      val aMock = mock[Foo]

      (new RuntimeException("Boom"): Throwable) willBe raised by aMock.returnsFuture("bye")
      ValueClass("mocked!") willBe returnedF by aMock.returnsFuture("hello")

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))
    }
  }
}
