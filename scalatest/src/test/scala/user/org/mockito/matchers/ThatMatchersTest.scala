package user.org.mockito.matchers

import org.mockito.exceptions.verification.WantedButNotInvoked
import org.mockito.matchers.EqTo
import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ThatMatchersTest extends AnyFlatSpec with MockitoSugar with Matchers with ArgumentMatchersSugar {
  "argMatching[T]" should "work in various scenarios" in {
    val aMock = mock[Foo]

    aMock.bar("meh")
    verify(aMock).bar(argMatching { case "meh" => })

    aMock.barTyped("meh")
    verify(aMock).barTyped(argMatching { case "meh" => })

    aMock.bar(List("meh"))
    verify(aMock).bar(argMatching { case "meh" :: Nil => })

    aMock.baz(Baz("Hello", "World"))
    verify(aMock).baz(argMatching { case Baz("Hello", "World") => })
    verify(aMock).baz(argMatching { case Baz(_, "World") => })
    verify(aMock).baz(argMatching { case Baz("Hello", _) => })
    verify(aMock).baz(argMatching { case Baz(_, _) => })

    an[WantedButNotInvoked] should be thrownBy {
      verify(aMock).baz(argMatching { case Baz("", _) => })
    }
  }

  "argThat[T]" should "work with AnyRef" in {
    val aMock = mock[Foo]

    aMock.bar("meh")
    verify(aMock).bar(argThat(EqTo("meh")))

    aMock.barTyped("meh")
    verify(aMock).barTyped(argThat(EqTo("meh")))

    aMock.bar(Seq("meh"))
    verify(aMock).bar(argThat(EqTo(Seq("meh"))))

    aMock.baz(Baz("Hello", "World"))
    verify(aMock).baz(argThat(EqTo(Baz("Hello", "World"))))
  }

  "argThat[T]" should "work with AnyVal" in {
    val aMock = mock[Foo]

    aMock.barByte(1)
    verify(aMock).barByte(argThat(EqTo(1.toByte)))

    aMock.barBoolean(false)
    verify(aMock).barBoolean(argThat(EqTo(false)))

    aMock.barChar('a')
    verify(aMock).barChar(argThat(EqTo('a')))

    aMock.barDouble(1d)
    verify(aMock).barDouble(argThat(EqTo(1d)))

    aMock.barInt(1)
    verify(aMock).barInt(argThat(EqTo(1)))

    aMock.barFloat(1)
    verify(aMock).barFloat(argThat(EqTo(1f)))

    aMock.barShort(1)
    verify(aMock).barShort(argThat(EqTo(1.toShort)))

    aMock.barLong(1)
    verify(aMock).barLong(argThat(EqTo(1L)))
  }

  "primitiveThat[T]" should "work with AnyVal" in {
    val aMock = mock[Foo]

    aMock.barByte(1)
    verify(aMock).barByte(byteThat(EqTo(1.toByte)))

    aMock.barBoolean(false)
    verify(aMock).barBoolean(booleanThat(EqTo(false)))

    aMock.barChar('a')
    verify(aMock).barChar(charThat(EqTo('a')))

    aMock.barDouble(1d)
    verify(aMock).barDouble(doubleThat(EqTo(1d)))

    aMock.barInt(1)
    verify(aMock).barInt(intThat(EqTo(1)))

    aMock.barFloat(1)
    verify(aMock).barFloat(floatThat(EqTo(1)))

    aMock.barShort(1)
    verify(aMock).barShort(shortThat(EqTo(1.toShort)))

    aMock.barLong(1)
    verify(aMock).barLong(longThat(EqTo(1L)))
  }
}
