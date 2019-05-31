package org.mockito.scalaz

import _root_.scalaz._
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Promise }

class IdiomaticMockitoScalazTest
    extends WordSpec
    with Matchers
    with IdiomaticMockito
    with ArgumentMatchersSugar
    with IdiomaticMockitoScalaz
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

      aMock.returnsGenericOption("hello") shouldReturnF "mocked!"

      aMock.returnsGenericOption("hello").value shouldBe "mocked!"
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

    "work with scalaz Eq" in {
      implicit val stringEq: Equal[ValueClass] = new Equal[ValueClass] {
        override def equal(x: ValueClass, y: ValueClass): Boolean = x.s.toLowerCase == y.s.toLowerCase
      }
      val aMock = mock[Foo]

      aMock.returnsGenericOption(ValueClass("HoLa")) shouldReturnF ValueClass("Mocked!")
      aMock.shouldI(false) shouldReturn "Mocked!"
      aMock.shouldI(true) shouldReturn "Mocked again!"

      aMock.returnsGenericOption(ValueClass("HOLA")).value should ===(ValueClass("mocked!"))
      aMock.shouldI(false) shouldBe "Mocked!"
      aMock.shouldI(true) shouldBe "Mocked again!"
    }

    "mix with vanilla api (scalaz -> vanilla)" in {
      val aMock            = mock[Foo]
      val unrealisedFuture = Promise[ValueClass]()

      aMock.returnsFuture("bye") shouldFailWith new RuntimeException("Boom") andThen Future.failed(new RuntimeException("Boom2"))
      aMock.returnsFuture("hello") shouldReturnF ValueClass("mocked!") andThen unrealisedFuture.future

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom2")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))

      unrealisedFuture.success(ValueClass("mocked2!"))
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked2!"))
    }

    "work with futures" in {
      val aMock = mock[Foo]

      aMock.returnsFuture("bye") shouldFailWith new RuntimeException("Boom")
      aMock.returnsFuture("hello") shouldReturnF ValueClass("mocked!")

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))
    }

    "work with EitherT" in {
      val aMock = mock[Foo]

      aMock.returnsEitherT("bye") shouldFailWith Error("error")
      aMock.returnsEitherT("hello") shouldReturnF ValueClass("mocked!")

      whenReady(aMock.returnsEitherT("bye").run)(_.toEither.left.value shouldBe Error("error"))
      whenReady(aMock.returnsEitherT("hello").run)(_.toEither.right.value shouldBe ValueClass("mocked!"))
    }

    "work with OptionT" in {
      val aMock = mock[Foo]

      aMock.returnsOptionT("hello") shouldReturnF ValueClass("mocked!")

      aMock.returnsOptionT("hello").run.head.value shouldBe ValueClass("mocked!")
    }
  }
}
