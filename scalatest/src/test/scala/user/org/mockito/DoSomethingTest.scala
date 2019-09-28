package user.org.mockito

import java.util.concurrent.atomic.AtomicInteger

import org.mockito.invocation.InvocationOnMock
import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.{ WordSpec, Matchers => ScalaTestMatchers }
import user.org.mockito.matchers.ValueCaseClassInt

class DoSomethingTest extends WordSpec with MockitoSugar with ScalaTestMatchers with ArgumentMatchersSugar {

  class Foo {
    def bar = "not mocked"

    def returnDouble: Double = ???

    def returnFloat: Float = ???

    def returnLong: Long = ???

    def returnInt: Int = ???

    def returnShort: Short = ???

    def returnByte: Byte = ???

    def returnChar: Char = ???

    def returnBoolean: Boolean = ???

    def iHaveSomeDefaultArguments(noDefault: String, default: String = "default value"): String = ???

    def doSomethingWithThisIntAndString(v: Int, v2: String): ValueCaseClassInt = ???

    def returnValueClass: ValueCaseClassInt = ???
  }

  "doCallRealMethod" should {
    "work as normal" in {
      val aMock = mock[Foo]

      doCallRealMethod.when(aMock).bar

      aMock.bar shouldBe "not mocked"
    }
  }

  "doAnswer" should {
    "work as normal" in {
      val aMock = mock[Foo]

      val counter = new AtomicInteger(1)
      doAnswer(counter.getAndIncrement().toString).when(aMock).bar

      counter.get shouldBe 1
      aMock.bar shouldBe "1"
      counter.get shouldBe 2
      aMock.bar shouldBe "2"
    }

    "work with a no arg function" in {
      val aMock = mock[Foo]

      val counter = new AtomicInteger(1)
      doAnswer(() => counter.getAndIncrement().toString).when(aMock).bar

      counter.get shouldBe 1
      aMock.bar shouldBe "1"
      counter.get shouldBe 2
      aMock.bar shouldBe "2"
    }

    "simplify answer API" in {
      val aMock = mock[Foo]

      doAnswer((i: Int, s: String) => ValueCaseClassInt(i * 10 + s.toInt)).when(aMock).doSomethingWithThisIntAndString(*, *)

      aMock.doSomethingWithThisIntAndString(4, "2") shouldBe ValueCaseClassInt(42)
    }

    "simplify answer API (invocation usage)" in {
      val aMock = mock[Foo]

      doAnswer((i: InvocationOnMock) => ValueCaseClassInt(i.arg[Int](0) * 10 + i.arg[String](1).toInt))
        .when(aMock)
        .doSomethingWithThisIntAndString(*, *)

      aMock.doSomethingWithThisIntAndString(4, "2") shouldBe ValueCaseClassInt(42)
    }
  }

  "doReturn" should {
    "not fail with overloading issues (one param)" in {
      val aMock = mock[Foo]

      doReturn("mocked!").when(aMock).bar

      aMock.bar shouldBe "mocked!"
    }

    "not fail with overloading issues (multi param)" in {
      val aMock = mock[Foo]

      doReturn("mocked!", "mocked again!").when(aMock).bar

      aMock.bar shouldBe "mocked!"
      aMock.bar shouldBe "mocked again!"
    }

    "work with AnyVals (one param)" in {
      val aMock = mock[Foo]

      doReturn(999d).when(aMock).returnDouble
      aMock.returnDouble shouldBe 999

      doReturn(999f).when(aMock).returnFloat
      aMock.returnFloat shouldBe 999

      doReturn(999L).when(aMock).returnLong
      aMock.returnLong shouldBe 999

      doReturn(999).when(aMock).returnInt
      aMock.returnInt shouldBe 999

      doReturn(255.toShort).when(aMock).returnShort
      aMock.returnShort shouldBe 255

      doReturn(128.toByte).when(aMock).returnByte
      aMock.returnByte shouldBe 128.toByte

      doReturn('c').when(aMock).returnChar
      aMock.returnChar shouldBe 'c'

      doReturn(false).when(aMock).returnBoolean
      aMock.returnBoolean shouldBe false
    }

    "work with AnyVals (multi param)" in {
      val aMock = mock[Foo]

      doReturn(999d, 111d).when(aMock).returnDouble
      aMock.returnDouble shouldBe 999
      aMock.returnDouble shouldBe 111

      doReturn(999f, 111f).when(aMock).returnFloat
      aMock.returnFloat shouldBe 999
      aMock.returnFloat shouldBe 111

      doReturn(999L, 111L).when(aMock).returnLong
      aMock.returnLong shouldBe 999
      aMock.returnLong shouldBe 111

      doReturn(999, 111).when(aMock).returnInt
      aMock.returnInt shouldBe 999
      aMock.returnInt shouldBe 111

      doReturn(255.toShort, 111.toShort).when(aMock).returnShort
      aMock.returnShort shouldBe 255
      aMock.returnShort shouldBe 111

      doReturn(128.toByte, 111.toByte).when(aMock).returnByte
      aMock.returnByte shouldBe 128.toByte
      aMock.returnByte shouldBe 111.toByte

      doReturn('c', 'z').when(aMock).returnChar
      aMock.returnChar shouldBe 'c'
      aMock.returnChar shouldBe 'z'

      doReturn(false, true).when(aMock).returnBoolean
      aMock.returnBoolean shouldBe false
      aMock.returnBoolean shouldBe true

      doReturn(ValueCaseClassInt(100), ValueCaseClassInt(200)).when(aMock).returnValueClass
      aMock.returnValueClass shouldBe ValueCaseClassInt(100)
      aMock.returnValueClass shouldBe ValueCaseClassInt(200)
    }
  }

  "doThrow" should {
    "work as normal (instance)" in {
      val aMock = mock[Foo]

      doThrow(new IllegalArgumentException).when(aMock).bar

      a[IllegalArgumentException] shouldBe thrownBy(aMock.bar)
    }

    "work as normal (class)" in {
      val aMock = mock[Foo]

      doThrow[IllegalArgumentException].when(aMock).bar

      a[IllegalArgumentException] shouldBe thrownBy(aMock.bar)
    }
  }
}
