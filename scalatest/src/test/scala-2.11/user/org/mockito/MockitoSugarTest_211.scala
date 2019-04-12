package user.org.mockito

import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.{ Matchers, WordSpec }

class MockitoSugarTest_211 extends WordSpec with MockitoSugar with Matchers with ArgumentMatchersSugar {

  trait Baz {
    def traitMethod(defaultArg: Int = 30, anotherDefault: String = "hola"): Int = ???
  }

  "mock[T]" should {
    "not fail with default arguments in traits" in {
      val aMock = mock[Baz]

      when(aMock.traitMethod(any, any)) thenReturn 69

      aMock.traitMethod() shouldBe 69

      verify(aMock).traitMethod(0, "")
    }
  }
}
