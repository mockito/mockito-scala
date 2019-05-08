package org.mockito.cats

import cats.Eq
import cats.implicits._
import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.{ EitherValues, Matchers, OptionValues, WordSpec }

class MockitoCatsEqTest
    extends WordSpec
    with Matchers
    with MockitoSugar
    with ArgumentMatchersSugar
    with MockitoCats
    with EitherValues
    with OptionValues {

  "mock[T]" should {
    "work with cats Eq" in {
      implicit val stringEq: Eq[ValueClass] = Eq.instance((x: ValueClass, y: ValueClass) => x.s.toLowerCase == y.s.toLowerCase)
      val aMock = mock[Foo]

      whenF(aMock.returnsOptionT(eqTo(ValueClass("HoLa")))) thenReturn ValueClass("Mocked!")

      aMock.returnsOptionT(ValueClass("HOLA")) should ===(Some(ValueClass("mocked!")))
    }
  }
}
