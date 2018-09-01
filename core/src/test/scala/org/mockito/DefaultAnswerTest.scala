package org.mockito

import org.scalatest
import org.mockito.exceptions.verification.SmartNullPointerException
import org.mockito.DefaultAnswerTest._
import org.mockito.exceptions.base.MockitoException
import org.mockito.invocation.InvocationOnMock
import org.scalatest.{OptionValues, TryValues, WordSpec}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future
import scala.util.{Success, Try}

object DefaultAnswerTest {
  class Foo {
    def bar(a: String) = "bar"

    def baz(a: String = "default"): String = a

    def valueClass: ValueClass = ValueClass(42)

    def userClass(v: Int = 42): Bar = new Bar

    def returnsList: List[String] = List("not mocked!")
  }

  case class ValueClass(v: Int) extends AnyVal

  class Bar {
    def callMeMaybe(): Unit = ()
  }

  class KnownTypes {
    def returnsOption: Option[String]           = Some("not mocked!")
    def returnsList: List[String]               = List("not mocked!")
    def returnsSet: Set[String]                 = Set("not mocked!")
    def returnsSeq: Seq[String]                 = Seq("not mocked!")
    def returnsIterable: Iterable[String]       = Iterable("not mocked!")
    def returnsTraversable: Traversable[String] = Traversable("not mocked!")
    def returnsIndexedSeq: IndexedSeq[String]   = IndexedSeq("not mocked!")
    def returnsIterator: Iterator[String]       = Iterator("not mocked!")
    def returnsStream: Stream[String]           = Stream("not mocked!")
    def returnsVector: Vector[String]           = Vector("not mocked!")
    def returnsEither: Either[Boolean, String]  = Right("not mocked!")
    def returnsTry: Try[String]                 = Success("not mocked!")
    def returnsFuture: Future[String]           = Future.successful("not mocked!")
    def returnsBigDecimal: BigDecimal           = BigDecimal(42)
    def returnsBigInt: BigInt                   = BigInt(42)
    def returnsStringBuilder: StringBuilder     = StringBuilder.newBuilder.append("not mocked!")
  }

  class Primitives {
    def barByte: Byte       = 1.toByte
    def barBoolean: Boolean = true
    def barChar: Char       = '1'
    def barDouble: Double   = 1
    def barInt: Int         = 1
    def barFloat: Float     = 1
    def barShort: Short     = 1
    def barLong: Long       = 1
  }
}

class DefaultAnswerTest
    extends WordSpec
    with scalatest.Matchers
    with IdiomaticMockito
    with TryValues
    with OptionValues
    with ScalaFutures {

  "DefaultAnswer" should {
    "resolve default parameters" in {
      val aMock: Foo = mock[Foo](new DefaultAnswer {
        override def apply(v1: InvocationOnMock): Option[Any] = None
      })

      aMock baz ()

      aMock wasCalled on baz "default"
    }
  }

  "DefaultAnswer.defaultAnswer" should {
    val aMock: Foo = mock[Foo](DefaultAnswer.defaultAnswer)

    "call real method for default arguments" in {
      aMock baz ()

      aMock wasCalled on baz "default"
    }

    "return a smart null for unknown cases" in {
      val smartNull: Bar = aMock.userClass()

      smartNull should not be null

      val throwable = the[SmartNullPointerException] thrownBy {
        smartNull.callMeMaybe()
      }

      throwable.getMessage should include("You have a NullPointerException here:")
    }

    "return a smart standard monad" in {
      val smartNull: List[String] = aMock.returnsList

      smartNull should not be null

      val throwable: SmartNullPointerException = the[SmartNullPointerException] thrownBy {
        smartNull.isEmpty
      }

      throwable.getMessage should include("You have a NullPointerException here:")
    }

    "return a default value for primitives" in {
      val primitives = mock[Primitives]

      primitives.barByte shouldBe 0.toByte
      primitives.barBoolean shouldBe false
      primitives.barChar shouldBe 0
      primitives.barDouble shouldBe 0
      primitives.barInt shouldBe 0
      primitives.barFloat shouldBe 0
      primitives.barShort shouldBe 0
      primitives.barLong shouldBe 0
    }

    "work for value classes" in {
      aMock.valueClass.v shouldBe 0
    }
  }

  "ReturnsEmptyValues" should {
    "return a default value for primitives" in {
      val primitives = mock[Primitives](ReturnsEmptyValues)

      primitives.barByte shouldBe 0.toByte
      primitives.barBoolean shouldBe false
      primitives.barChar shouldBe 0
      primitives.barDouble shouldBe 0
      primitives.barInt shouldBe 0
      primitives.barFloat shouldBe 0
      primitives.barShort shouldBe 0
      primitives.barLong shouldBe 0
    }

    "return the empty values for known classes" in {
      val aMock = mock[KnownTypes](ReturnsEmptyValues)

      aMock.returnsOption shouldBe None
      aMock.returnsList shouldBe List.empty
      aMock.returnsSet shouldBe Set.empty
      aMock.returnsSeq shouldBe Seq.empty
      aMock.returnsIterable shouldBe Iterable.empty
      aMock.returnsTraversable shouldBe Traversable.empty
      aMock.returnsIndexedSeq shouldBe IndexedSeq.empty
      aMock.returnsIterator shouldBe Iterator.empty
      aMock.returnsStream shouldBe Stream.empty
      aMock.returnsVector shouldBe Vector.empty
      aMock.returnsTry.failure.exception shouldBe a[MockitoException]
      aMock.returnsTry.failure.exception.getMessage shouldBe "Auto stub provided by mockito-scala"
      aMock.returnsFuture.failed.value.value.success.value shouldBe a[MockitoException]
      aMock.returnsFuture.failed.value.value.success.value.getMessage shouldBe "Auto stub provided by mockito-scala"
      aMock.returnsBigDecimal shouldBe BigDecimal(0)
      aMock.returnsBigInt shouldBe BigInt(0)
      aMock.returnsStringBuilder shouldBe StringBuilder.newBuilder
    }
  }
}
