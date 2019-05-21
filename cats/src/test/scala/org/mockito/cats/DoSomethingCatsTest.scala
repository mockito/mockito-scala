package org.mockito.cats

import cats.implicits._
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }

import scala.concurrent.ExecutionContext.Implicits.global

class DoSomethingCatsTest
    extends WordSpec
    with Matchers
    with IdiomaticMockito
    with ArgumentMatchersSugar
    with IdiomaticMockitoCats
    with EitherValues
    with OptionValues
    with ScalaFutures {

  "willBe returnedF by" should {
    "stub full applicative" in {
      val aMock = mock[Foo]

      "mocked!" willBe returnedF by aMock.returnsOptionString(*)

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "stub specific applicative" in {
      val aMock = mock[Foo]

      "mocked!" willBe returnedF by aMock.returnsGenericOption("hello")

      aMock.returnsGenericOption("hello").value shouldBe "mocked!"
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

    "work with EitherT" in {
      val aMock = mock[Foo]

      Error("error") willBe raised by aMock.returnsEitherT("bye")
      ValueClass("mocked!") willBe returnedF by aMock.returnsEitherT("hello")

      whenReady(aMock.returnsEitherT("bye").value)(_.left.value shouldBe Error("error"))
      whenReady(aMock.returnsEitherT("hello").value)(_.right.value shouldBe ValueClass("mocked!"))
    }

    "work with OptionT" in {
      val aMock = mock[Foo]

      ValueClass("mocked!") willBe returnedF by aMock.returnsOptionT("hello")

      aMock.returnsOptionT("hello").value.head.value shouldBe ValueClass("mocked!")
    }
  }
}
