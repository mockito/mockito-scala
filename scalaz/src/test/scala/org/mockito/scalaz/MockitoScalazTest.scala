package org.mockito.scalaz

import _root_.scalaz._
import Scalaz._
import org.mockito.invocation.InvocationOnMock
import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ EitherValues, OptionValues }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MockitoScalazTest extends AnyWordSpec with Matchers with MockitoSugar with ArgumentMatchersSugar with MockitoScalaz with EitherValues with OptionValues with ScalaFutures {
  "when - return" should {
    "stub full applicative" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsOptionString(*)) thenReturn "mocked!"

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "stub specific applicative" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsGenericOption("hello")) thenReturn "mocked!"

      aMock.returnsGenericOption("hello").value shouldBe "mocked!"
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

    "work with scalaz Eq" in {
      implicit val stringEq: Equal[ValueClass] = new Equal[ValueClass] {
        override def equal(x: ValueClass, y: ValueClass): Boolean = x.s.toLowerCase == y.s.toLowerCase
      }
      val aMock = mock[Foo]

      whenF(aMock.returnsGenericOption(eqTo(ValueClass("HoLa")))) thenReturn ValueClass("Mocked!")
      when(aMock.shouldI(eqTo(false))) thenReturn "Mocked!"
      when(aMock.shouldI(eqTo(true))) thenReturn "Mocked again!"

      aMock.returnsGenericOption(ValueClass("HOLA")).value should ===(ValueClass("mocked!"))
      aMock.shouldI(false) shouldBe "Mocked!"
      aMock.shouldI(true) shouldBe "Mocked again!"
    }

    "work with futures" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsFuture("bye")) thenFailWith new RuntimeException("Boom")
      whenF(aMock.returnsFuture("hello")) thenReturn ValueClass("mocked!")

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))
    }

    "work with EitherT" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsEitherT("bye")) thenFailWith Error("error")
      whenF(aMock.returnsEitherT("hello")) thenReturn ValueClass("mocked!")

      whenReady(aMock.returnsEitherT("bye").run)(_.toEither.left.value shouldBe Error("error"))
      whenReady(aMock.returnsEitherT("hello").run)(_.toEither.right.value shouldBe ValueClass("mocked!"))
    }

    "work with OptionT" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsOptionT("hello")) thenReturn ValueClass("mocked!")

      aMock.returnsOptionT("hello").run.head.value shouldBe ValueClass("mocked!")
    }
  }

  "when - answer" should {
    "stub single applicative" in {
      val aMock = mock[Foo]

      whenF(aMock.returnsOptionString("hello")) thenAnswer "mocked!"
      whenF(aMock.returnsOptionString("hi")) thenAnswer ((s: String) => s + " mocked!")
      whenF(aMock.returnsOptionString("hola")) thenAnswer ((i: InvocationOnMock) => i.arg[String](0) + " invocation mocked!")
      whenF(aMock.returnsOptionFrom(42, true)) thenAnswer ((i: Int, b: Boolean) => s"$i, $b")

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
      aMock.returnsOptionString("hi").value shouldBe "hi mocked!"
      aMock.returnsOptionString("hola").value shouldBe "hola invocation mocked!"
      aMock.returnsOptionFrom(42, true).value shouldBe "42, true"
    }

    "stub composed applicative" in {
      val aMock = mock[Foo]

      whenFG(aMock.returnsFutureEither("hello")) thenAnswer ValueClass("mocked!")
      whenFG(aMock.returnsFutureEither("hi")) thenAnswer ((s: String) => ValueClass(s + " mocked!"))
      whenFG(aMock.returnsFutureEither("hola")) thenAnswer ((i: InvocationOnMock) => ValueClass(i.arg[String](0) + " invocation mocked!"))
      whenFG(aMock.returnsFutureOptionFrom(42, true)) thenAnswer ((i: Int, b: Boolean) => s"$i, $b")

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("hi"))(_.right.value shouldBe ValueClass("hi mocked!"))
      whenReady(aMock.returnsFutureEither("hola"))(_.right.value shouldBe ValueClass("hola invocation mocked!"))
      whenReady(aMock.returnsFutureOptionFrom(42, true))(_.value shouldBe "42, true")
    }
  }

  "doReturn" should {
    "stub full applicative" in {
      val aMock = mock[Foo]

      doReturnF[Option, String]("mocked!").when(aMock).returnsOptionString(*)

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
    }

    "stub specific applicative" in {
      val aMock = mock[Foo]

      doReturnF[Option, String]("mocked!").when(aMock).returnsGenericOption("hello")

      aMock.returnsGenericOption("hello").value shouldBe "mocked!"
    }

    "stub generic applicative" in {
      val aMock = mock[Foo]

      doReturnF[Option, String]("mocked!").when(aMock).returnsMT("hello")

      aMock.returnsMT[Option, String]("hello").value shouldBe "mocked!"
    }

    "stub composed applicative" in {
      val aMock = mock[Foo]

      doReturnFG[Future, ErrorOr, ValueClass](ValueClass("mocked!")).when(aMock).returnsFutureEither("hello")
      doFailWithG[Future, ErrorOr, Error, ValueClass](Error("boom")).when(aMock).returnsFutureEither("bye")

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("bye"))(_.left.value shouldBe Error("boom"))
    }

    "work with value classes" in {
      val aMock = mock[Foo]

      doReturnF[Option, ValueClass](ValueClass("mocked!")).when(aMock).returnsMT(eqTo(ValueClass("hi")))

      aMock.returnsMT[Option, ValueClass](ValueClass("hi")).value shouldBe ValueClass("mocked!")
    }

    "raise errors" in {
      val aMock = mock[Foo]

      doReturnF[ErrorOr, ValueClass](ValueClass("mocked!")).when(aMock).returnsMT(ValueClass("hi"))
      doFailWith[ErrorOr, Error, ValueClass](Error("error")).when(aMock).returnsMT(ValueClass("bye"))

      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("hi")).right.value shouldBe ValueClass("mocked!")
      aMock.returnsMT[ErrorOr, ValueClass](ValueClass("bye")).left.value shouldBe Error("error")
    }

    "work with futures" in {
      val aMock = mock[Foo]

      doFailWith[Future, Throwable, ValueClass](new RuntimeException("Boom")).when(aMock).returnsFuture("bye")
      doReturnF[Future, ValueClass](ValueClass("mocked!")).when(aMock).returnsFuture("hello")

      whenReady(aMock.returnsFuture("bye").failed)(_.getMessage shouldBe "Boom")
      whenReady(aMock.returnsFuture("hello"))(_ shouldBe ValueClass("mocked!"))
    }

    "work with EitherT" in {
      val aMock = mock[Foo]
      type F[T] = EitherT[Future, Error, T]

      doFailWith[F, Error, ValueClass](Error("error")).when(aMock).returnsEitherT("bye")
      doReturnF[F, ValueClass](ValueClass("mocked!")).when(aMock).returnsEitherT("hello")

      whenReady(aMock.returnsEitherT("bye").run)(_.toEither.left.value shouldBe Error("error"))
      whenReady(aMock.returnsEitherT("hello").run)(_.toEither.right.value shouldBe ValueClass("mocked!"))
    }

    "work with OptionT" in {
      val aMock = mock[Foo]
      type F[T] = OptionT[List, T]

      doReturnF[F, ValueClass](ValueClass("mocked!")).when(aMock).returnsOptionT("hello")

      aMock.returnsOptionT("hello").run.head.value shouldBe ValueClass("mocked!")
    }
  }

  "doAnswer" should {
    "stub single applicative" in {
      val aMock = mock[Foo]

      doAnswerF[Option, String]("mocked!").when(aMock).returnsOptionString("hello")
      doAnswerF[Option, String, String]((s: String) => s + " mocked!").when(aMock).returnsOptionString("hi")
      doAnswerF[Option, InvocationOnMock, String]((i: InvocationOnMock) => i.arg[String](0) + " invocation mocked!")
        .when(aMock)
        .returnsOptionString("hola")
      doAnswerF[Option, Int, Boolean, String]((i: Int, b: Boolean) => s"$i, $b").when(aMock).returnsOptionFrom(42, true)

      aMock.returnsOptionString("hello").value shouldBe "mocked!"
      aMock.returnsOptionString("hi").value shouldBe "hi mocked!"
      aMock.returnsOptionString("hola").value shouldBe "hola invocation mocked!"
      aMock.returnsOptionFrom(42, true).value shouldBe "42, true"
    }

    "stub composed applicative" in {
      val aMock = mock[Foo]

      doAnswerFG[Future, ErrorOr, ValueClass](ValueClass("mocked!")).when(aMock).returnsFutureEither("hello")
      doAnswerFG[Future, ErrorOr, String, ValueClass]((s: String) => ValueClass(s + " mocked!")).when(aMock).returnsFutureEither("hi")
      doAnswerFG[Future, ErrorOr, InvocationOnMock, ValueClass] { i: InvocationOnMock =>
        ValueClass(i.arg[String](0) + " invocation mocked!")
      }.when(aMock)
        .returnsFutureEither("hola")
      doAnswerFG[Future, Option, Int, Boolean, String]((i: Int, b: Boolean) => s"$i, $b").when(aMock).returnsFutureOptionFrom(42, true)

      whenReady(aMock.returnsFutureEither("hello"))(_.right.value shouldBe ValueClass("mocked!"))
      whenReady(aMock.returnsFutureEither("hi"))(_.right.value shouldBe ValueClass("hi mocked!"))
      whenReady(aMock.returnsFutureEither("hola"))(_.right.value shouldBe ValueClass("hola invocation mocked!"))
      whenReady(aMock.returnsFutureOptionFrom(42, true))(_.value shouldBe "42, true")
    }
  }
}
