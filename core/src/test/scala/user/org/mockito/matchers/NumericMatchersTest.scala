package user.org.mockito.matchers

import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.mockito.exceptions.verification.WantedButNotInvoked
import org.scalatest.{ FlatSpec, Matchers }

class NumericMatchersTest extends FlatSpec with IdiomaticMockito with Matchers with ArgumentMatchersSugar {

  class Foo {
    def pepe[N](n: N, v: String = "meh"): N = ???
  }

  ">" should "work with any Numeric" in {
    val aMock = mock[Foo]

    aMock.pepe(4)
    aMock.pepe(4.1)
    aMock.pepe(BigDecimal(4.2))
    aMock.pepe(5)

    aMock.pepe(n > 3.0) wasCalled fourTimes

    an[WantedButNotInvoked] shouldBe thrownBy {
      aMock.pepe(n > 5) was called
    }
  }

  ">=" should "work with any Numeric" in {
    val aMock = mock[Foo]

    aMock.pepe(4)
    aMock.pepe(4.1)
    aMock.pepe(BigDecimal(4.2))
    aMock.pepe(4.99999)

    aMock.pepe(n >= 3.0) wasCalled fourTimes

    an[WantedButNotInvoked] shouldBe thrownBy {
      aMock.pepe(n >= 5) was called
    }
  }

  "<" should "work with any Numeric" in {
    val aMock = mock[Foo]

    aMock.pepe(4)
    aMock.pepe(4.1)
    aMock.pepe(BigDecimal(4.2))
    aMock.pepe(4.99999)

    aMock.pepe(n < 5) wasCalled fourTimes

    an[WantedButNotInvoked] shouldBe thrownBy {
      aMock.pepe(n < 3.0) was called
    }
  }

  "<=" should "work with any Numeric" in {
    val aMock = mock[Foo]

    aMock.pepe(4)
    aMock.pepe(4.1)
    aMock.pepe(BigDecimal(4.2))
    aMock.pepe(5)

    aMock.pepe(n <= 5) wasCalled fourTimes

    an[WantedButNotInvoked] shouldBe thrownBy {
      aMock.pepe(n <= 3.0) was called
    }
  }

  "=~" should "work" in {
    val aMock = mock[Foo]

    aMock.pepe(4.999)

    aMock.pepe(n =~ 5.0 +- 0.001) was called

    an[WantedButNotInvoked] shouldBe thrownBy {
      aMock.pepe(n =~ 5.0 +- 0.00001) was called
    }
  }
}
