package org.mockito

import org.scalatest
import org.mockito.exceptions.verification.SmartNullPointerException
import org.mockito.ScalaDefaultAnswerTest._
import org.mockito.exceptions.base.MockitoException
import org.scalatest.{EitherValues, FlatSpec, TryValues}

import scala.util.{Success, Try}

object ScalaDefaultAnswerTest {
  class Foo {
    def bar(a: String) = "bar"

    def baz(a: String = "default"): String = a

    def some(i: Int): Option[String] = None

    def valueClass: ValueClass = ValueClass(42)

    def userClass(v: Int = 42): Bar = new Bar
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
    def returnsBigDecimal: BigDecimal           = BigDecimal(42)
    def returnsBigInt: BigInt                   = BigInt(42)
    def returnsStringBuilder: StringBuilder     = StringBuilder.newBuilder.append("not mocked!")
  }
}

class ScalaDefaultAnswerTest extends FlatSpec with scalatest.Matchers with IdiomaticMockito with TryValues with EitherValues {

  trait Setup {
    val aMock: Foo = mock[Foo](ScalaDefaultAnswer)
  }

  it should "call real method for default arguments" in new Setup {
    aMock baz ()

    aMock wasCalled on baz "default"
  }

  it should "return an empty instance for a known class" in new Setup {
    aMock.some(42) shouldBe empty
  }

  it should "return a smart null for unknown cases" in new Setup {
    val smartNull: Bar = aMock.userClass()

    smartNull should not be null

    val throwable: SmartNullPointerException = the[SmartNullPointerException] thrownBy {
      smartNull.callMeMaybe()
    }

    throwable.getMessage shouldBe
    s"""You have a NullPointerException because this method call was *not* stubbed correctly:
        |[foo.userClass(42);] on the Mock [$aMock]""".stripMargin
  }

  it should "return a smart value for value classes" in new Setup {
    aMock.valueClass.v shouldBe 0
  }

  it should "return the empty values for known classes" in {
    val aMock = mock[KnownTypes](ScalaDefaultAnswer)

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
    aMock.returnsBigDecimal shouldBe BigDecimal(0)
    aMock.returnsBigInt shouldBe BigInt(0)
    aMock.returnsStringBuilder shouldBe StringBuilder.newBuilder
  }
}
