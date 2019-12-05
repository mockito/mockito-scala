package user.org.mockito.stubbing

import org.mockito.exceptions.base.MockitoException
import org.mockito.{ DefaultAnswers, IdiomaticMockito }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, OptionValues, TryValues, WordSpec }
import user.org.mockito.stubbing.DefaultAnswerTest._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.{ Success, Try }

class ReturnsEmptyValuesTest extends WordSpec with Matchers with IdiomaticMockito with TryValues with OptionValues with ScalaFutures {
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
    def returnsMap: Map[String, Int]            = Map("not mocked!" -> 42)
    def returnsMutableSeq: mutable.Seq[String]  = ListBuffer("not mocked!")
    def returnsListBuffer: ListBuffer[String]   = ListBuffer("not mocked!")
    def returnsMutableSet: mutable.Set[String]  = mutable.HashSet("not mocked!")
  }

  "ReturnsEmptyValues" should {
    "return a default value for primitives" in {
      val primitives = mock[Primitives](DefaultAnswers.ReturnsEmptyValues)

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
      val aMock = mock[KnownTypes](DefaultAnswers.ReturnsEmptyValues)

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
      aMock.returnsStringBuilder shouldBe new StringBuilder
      aMock.returnsEither shouldBe Left("Auto stub provided by mockito-scala")
      aMock.returnsMap shouldBe Map.empty
      aMock.returnsMutableSeq shouldBe ListBuffer.empty
      aMock.returnsListBuffer shouldBe ListBuffer.empty
      aMock.returnsMutableSet shouldBe mutable.HashSet.empty
    }
  }
}
