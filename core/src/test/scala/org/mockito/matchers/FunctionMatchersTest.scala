package org.mockito.matchers

import org.mockito.MockitoSugar
import org.mockito.exceptions.verification.ArgumentsAreDifferent
import org.scalatest.{ WordSpec, Matchers => ScalaTestMatchers }

class FunctionMatchersTest extends WordSpec with MockitoSugar with ScalaTestMatchers with FunctionMatchers {

  class Foo {
    def iHaveFunction0[T](v: () => T): T = v()
  }

  "function0[T]" should {

    "pass if return value matches" in {
      val aMock = mock[Foo]

      aMock.iHaveFunction0(() => "meh")

      verify(aMock).iHaveFunction0(function0("meh"))
    }

    "fail" in {
      val aMock = mock[Foo]

      aMock.iHaveFunction0(() => "not meh")

      an[ArgumentsAreDifferent] should be thrownBy verify(aMock).iHaveFunction0(function0("meh"))
    }
  }

}
