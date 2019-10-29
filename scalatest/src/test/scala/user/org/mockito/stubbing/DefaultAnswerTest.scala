package user.org.mockito.stubbing

import org.mockito.IdiomaticMockito
import org.mockito.exceptions.verification.SmartNullPointerException
import org.mockito.stubbing.DefaultAnswer
import org.scalatest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ OptionValues, TryValues, WordSpec }
import user.org.mockito.stubbing.DefaultAnswerTest._

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

class DefaultAnswerTest extends WordSpec with scalatest.Matchers with IdiomaticMockito with TryValues with OptionValues with ScalaFutures {

  "DefaultAnswer.defaultAnswer" should {
    val aMock: Foo = mock[Foo](DefaultAnswer.defaultAnswer)

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
}
