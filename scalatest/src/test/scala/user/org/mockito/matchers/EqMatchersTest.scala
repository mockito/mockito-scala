package user.org.mockito.matchers

import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalactic.{ Equality, StringNormalizations }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EqMatchersTest extends AnyFlatSpec with MockitoSugar with Matchers with ArgumentMatchersSugar {
  "eqTo[T]" should "work with value classes" in {
    val aMock = mock[Foo]

    aMock.valueClass(new ValueClass("meh"))
    verify(aMock).valueClass(eqTo(new ValueClass("meh")))

    aMock.valueCaseClass(ValueCaseClassInt(100))
    verify(aMock).valueCaseClass(eqTo(ValueCaseClassInt(100)))
    val expected = ValueCaseClassInt(100)
    verify(aMock).valueCaseClass(eqTo(expected))
  }

  "eqToVal[T]" should "work with value classes" in {
    val aMock = mock[Foo]

    aMock.valueClass(new ValueClass("meh"))
    verify(aMock).valueClass(eqToVal(new ValueClass("meh")))

    aMock.valueCaseClass(ValueCaseClassInt(100))
    verify(aMock).valueCaseClass(eqToVal(ValueCaseClassInt(100)))
    val expected = ValueCaseClassInt(100)
    verify(aMock).valueCaseClass(eqToVal(expected))
  }

  "eqTo[T]" should "work with AnyRef" in {
    val aMock = mock[Foo]

    aMock.bar("meh")
    verify(aMock).bar(eqTo("meh"))

    aMock.barTyped("meh")
    verify(aMock).barTyped(eqTo("meh"))

    aMock.bar(Seq("meh"))
    verify(aMock).bar(eqTo(Seq("meh")))

    aMock.baz(Baz("Hello", "World"))
    verify(aMock).baz(eqTo(Baz("Hello", "World")))
  }

  "eqTo[T]" should "work with AnyVal" in {
    val aMock = mock[Foo]

    aMock.barByte(1)
    verify(aMock).barByte(eqTo(1))

    aMock.barBoolean(false)
    verify(aMock).barBoolean(eqTo(false))

    aMock.barChar('a')
    verify(aMock).barChar(eqTo('a'))

    aMock.barDouble(1d)
    verify(aMock).barDouble(eqTo(1d))

    aMock.barInt(1)
    verify(aMock).barInt(eqTo(1))

    aMock.barFloat(1)
    verify(aMock).barFloat(eqTo(1))

    aMock.barShort(1)
    verify(aMock).barShort(eqTo(1))

    aMock.barLong(1)
    verify(aMock).barLong(eqTo(1L))
  }

  "same[T]" should "work with AnyRef" in {
    val aMock = mock[Foo]

    aMock.bar("meh")
    verify(aMock).bar(same("meh"))

    aMock.barTyped("meh")
    verify(aMock).barTyped(same("meh"))

    val seq = Seq("meh")
    aMock.bar(seq)
    verify(aMock).bar(same(seq))
  }

  "isA[T]" should "work with AnyRef" in {
    val aMock = mock[Foo]

    aMock.bar("meh")
    verify(aMock).bar(isA[String])

    aMock.barTyped("meh")
    verify(aMock).barTyped(isA[String])

    aMock.bar(Seq("meh"))
    verify(aMock).bar(isA[Seq[String]])
  }

  "isA[T]" should "work with AnyVal" in {
    val aMock = mock[Foo]

    aMock.barByte(1)
    verify(aMock).barByte(isA[Byte])

    aMock.barBoolean(false)
    verify(aMock).barBoolean(isA[Boolean])

    aMock.barChar('a')
    verify(aMock).barChar(isA[Char])

    aMock.barDouble(1d)
    verify(aMock).barDouble(isA[Double])

    aMock.barInt(1)
    verify(aMock).barInt(isA[Int])

    aMock.barFloat(1)
    verify(aMock).barFloat(isA[Float])

    aMock.barShort(1)
    verify(aMock).barShort(isA[Short])

    aMock.barLong(1)
    verify(aMock).barLong(isA[Long])
  }

  "refEq[T]" should "work on scala types" in {
    val aMock = mock[Foo]

    aMock.baz(Baz("Hello", "World"))
    verify(aMock).baz(refEq(Baz("Hello", "World")))
    verify(aMock).baz(refEq(Baz("Hello", "Mars"), "param2"))
  }

  "eqTo[T]" should "work when an implicit Equality is in scope" in {
    import StringNormalizations._

    implicit val eq: Equality[String] = decided by defaultEquality[String] afterBeing lowerCased

    val aMock = mock[Foo]

    aMock.bar("meh")
    verify(aMock).bar(eqTo("MEH"))
  }
}
