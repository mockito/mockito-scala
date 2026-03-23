package org.mockito

import org.mockito.matchers.DefaultValueProvider
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * Tests that run for ALL Scala versions (Scala 2 and 3).
 *
 * Verifies the shared contract of DefaultValueProvider and Captor macro materialisation, using only version-agnostic APIs.
 */
private case class MacroShared_UserId(value: Long)   extends AnyVal
private case class MacroShared_Tagged[T](value: Int) extends AnyVal

class MacroSubTest extends AnyWordSpec with Matchers {

  "DefaultValueProvider" should {
    "provide the zero-equivalent default for a value class" in {
      implicitly[DefaultValueProvider[MacroShared_UserId]].default shouldBe MacroShared_UserId(0L)
    }

    "provide null for String" in {
      implicitly[DefaultValueProvider[String]].default shouldBe null
    }

    "provide the zero-equivalent default for a value class with a phantom type parameter" in {
      implicitly[DefaultValueProvider[MacroShared_Tagged[String]]].default shouldBe MacroShared_Tagged[String](0)
    }
  }

  "Captor" should {
    "materialise for a plain type with no captured values initially" in {
      implicitly[captor.Captor[Int]].values shouldBe empty
    }

    "materialise for a value class with no captured values initially" in {
      implicitly[captor.Captor[MacroShared_UserId]].values shouldBe empty
    }

    "materialise for a value class with a phantom type parameter with no captured values initially" in {
      implicitly[captor.Captor[MacroShared_Tagged[String]]].values shouldBe empty
    }

    "use WrapperCaptor for plain types" in {
      implicitly[captor.Captor[Int]].getClass.getSimpleName should include("WrapperCaptor")
    }
  }
}
