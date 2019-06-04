package org.mockito.scalaz

import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }
import _root_.scalaz._
import Scalaz._
import org.mockito.invocation.InvocationOnMock

import scala.concurrent.ExecutionContext.Implicits.global

class DoSomethingScalazTest
    extends WordSpec
    with Matchers
    with IdiomaticMockito
    with ArgumentMatchersSugar
    with IdiomaticMockitoScalaz
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

      whenReady(aMock.returnsEitherT("bye").run)(_.toEither.left.value shouldBe Error("error"))
      whenReady(aMock.returnsEitherT("hello").run)(_.toEither.right.value shouldBe ValueClass("mocked!"))
    }

    "work with OptionT" in {
      val aMock = mock[Foo]

      ValueClass("mocked!") willBe returnedF by aMock.returnsOptionT("hello")

      aMock.returnsOptionT("hello").run.head.value shouldBe ValueClass("mocked!")
    }
  }

  "willBe answeredF by" should {
    "stub single applicative" in {
      val aMock = mock[Foo]

      "mocked!" willBe answeredF by aMock.returnsOptionString("hello")
      ((s: String) => s + " mocked!") willBe answeredF by aMock.returnsOptionString("hi")
      ((i: InvocationOnMock) => i.getArgument[String](0) + " invocation mocked!") willBe answeredF by aMock.returnsOptionString("hola")
      ((i: Int, b: Boolean) => s"$i, $b") willBe answeredF by aMock.returnsOptionFrom(42, true)

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
      aMock.returnsOptionString("hi").value shouldBe "hi mocked!"
      aMock.returnsOptionString("hola").value shouldBe "hola invocation mocked!"
      aMock.returnsOptionFrom(42, true).value shouldBe "42, true"
    }

    "stub composed applicative" in {
      val aMock = mock[Foo]

      ValueClass("mocked!") willBe answeredFG by aMock.returnsFutureEither("hello")
      ((s: String) => ValueClass(s + " mocked!")) willBe answeredFG by aMock.returnsFutureEither("hi")
      ((i: InvocationOnMock) => ValueClass(i.getArgument[String](0) + " invocation mocked!")) willBe answeredFG by aMock
        .returnsFutureEither("hola")
      ((i: Int, b: Boolean) => s"$i, $b") willBe answeredFG by aMock.returnsFutureOptionFrom(42, true)

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("hi"))(_.right.value shouldBe ValueClass("hi mocked!"))
      whenReady(aMock.returnsFutureEither("hola"))(_.right.value shouldBe ValueClass("hola invocation mocked!"))
      whenReady(aMock.returnsFutureOptionFrom(42, true))(_.value shouldBe "42, true")
    }
  }
}
