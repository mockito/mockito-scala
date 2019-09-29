package user.org.mockito.matchers

import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.{ FlatSpec, Matchers => ScalaTestMatchers }

class AnyMatchersTest extends FlatSpec with MockitoSugar with ScalaTestMatchers with ArgumentMatchersSugar {

  "any[Collection]" should "work with Scala types" in {
    val aMock = mock[Foo]

    when(aMock.barSeq(anySeq[String])) thenReturn Seq("mocked!")
    aMock.barSeq(Seq("meh")) shouldBe Seq("mocked!")
    verify(aMock).barSeq(Seq("meh"))

    when(aMock.barList(anyList[String])) thenReturn List("mocked!")
    aMock.barList(List("meh")) shouldBe List("mocked!")
    verify(aMock).barList(List("meh"))

    when(aMock.barIterable(anyIterable[String])) thenReturn Iterable("mocked!")
    aMock.barIterable(Iterable("meh")) shouldBe Iterable("mocked!")
    verify(aMock).barIterable(Iterable("meh"))

    when(aMock.barMap(anyMap[String, String])) thenReturn Map(
      "I am" -> "mocked!"
    )
    aMock.barMap(Map.empty) shouldBe Map("I am" -> "mocked!")
    verify(aMock).barMap(Map.empty)

    when(aMock.barSet(anySet[String])) thenReturn Set("mocked!")
    aMock.barSet(Set("meh")) shouldBe Set("mocked!")
    verify(aMock).barSet(Set("meh"))
  }

  "any" should "work with AnyRef" in {
    val aMock = mock[Foo]

    when(aMock.bar[String](any)) thenReturn "mocked!"
    aMock.bar("meh") shouldBe "mocked!"
    verify(aMock).bar("meh")

    when(aMock.barTyped(any)) thenReturn "mocked!"
    aMock.barTyped("meh") shouldBe "mocked!"
    verify(aMock).barTyped("meh")
  }

  "anyVal" should "work with a value class" in {
    val aMock = mock[Foo]

    when(aMock.valueClass(anyVal[ValueClass])) thenReturn "mocked!"
    aMock.valueClass(new ValueClass("meh")) shouldBe "mocked!"
    verify(aMock).valueClass(anyVal[ValueClass])

    when(aMock.valueCaseClass(anyVal[ValueCaseClassInt])) thenReturn 100
    aMock.valueCaseClass(ValueCaseClassInt(1)) shouldBe 100
    verify(aMock).valueCaseClass(anyVal[ValueCaseClassInt])
  }

  "any" should "work with a value class" in {
    val aMock = mock[Foo]

    when(aMock.valueClass(any[ValueClass])) thenReturn "mocked!"
    aMock.valueClass(new ValueClass("meh")) shouldBe "mocked!"
    verify(aMock).valueClass(any[ValueClass])

    when(aMock.valueCaseClass(any[ValueCaseClassInt])) thenReturn 100
    aMock.valueCaseClass(ValueCaseClassInt(1)) shouldBe 100
    verify(aMock).valueCaseClass(any[ValueCaseClassInt])
  }

  "any" should "work with AnyVal" in {
    val aMock = mock[Foo]

    when(aMock.barByte(any)) thenReturn 10.toByte
    aMock.barByte(1) shouldBe 10
    verify(aMock).barByte(1)

    when(aMock.barBoolean(any)) thenReturn true
    aMock.barBoolean(false) shouldBe true
    verify(aMock).barBoolean(false)

    when(aMock.barChar(any)) thenReturn 'c'
    aMock.barChar('a') shouldBe 'c'
    verify(aMock).barChar('a')

    when(aMock.barDouble(any)) thenReturn 100d
    aMock.barDouble(1d) shouldBe 100d
    verify(aMock).barDouble(1d)

    when(aMock.barInt(any)) thenReturn 100
    aMock.barInt(1) shouldBe 100
    verify(aMock).barInt(1)

    when(aMock.barFloat(any)) thenReturn 100f
    aMock.barFloat(1) shouldBe 100f
    verify(aMock).barFloat(1)

    when(aMock.barShort(any)) thenReturn 100.toShort
    aMock.barShort(1) shouldBe 100
    verify(aMock).barShort(1)

    when(aMock.barLong(any)) thenReturn 100L
    aMock.barLong(1) shouldBe 100L
    verify(aMock).barLong(1L)
  }

  "anyPrimitive" should "work with AnyVal" in {
    val aMock = mock[Foo]

    when(aMock.barByte(anyByte)) thenReturn 10.toByte
    aMock.barByte(1) shouldBe 10
    verify(aMock).barByte(1)

    when(aMock.barBoolean(anyBoolean)) thenReturn true
    aMock.barBoolean(false) shouldBe true
    verify(aMock).barBoolean(false)

    when(aMock.barChar(anyChar)) thenReturn 'c'
    aMock.barChar('a') shouldBe 'c'
    verify(aMock).barChar('a')

    when(aMock.barDouble(anyDouble)) thenReturn 100d
    aMock.barDouble(1d) shouldBe 100d
    verify(aMock).barDouble(1d)

    when(aMock.barInt(anyInt)) thenReturn 100
    aMock.barInt(1) shouldBe 100
    verify(aMock).barInt(1)

    when(aMock.barFloat(anyFloat)) thenReturn 100f
    aMock.barFloat(1) shouldBe 100f
    verify(aMock).barFloat(1)

    when(aMock.barShort(anyShort)) thenReturn 100.toShort
    aMock.barShort(1) shouldBe 100
    verify(aMock).barShort(1)

    when(aMock.barLong(anyLong)) thenReturn 100L
    aMock.barLong(1) shouldBe 100L
    verify(aMock).barLong(1L)
  }

  "*" should "be a valid alias of any" in {
    val aMock = mock[Foo]

    when(aMock.barInt(*)) thenReturn 42

    aMock.barInt(-1) shouldBe 42
  }
}
