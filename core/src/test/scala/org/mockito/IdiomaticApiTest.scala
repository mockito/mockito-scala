package org.mockito

import org.mockito.exceptions.verification.WantedButNotInvoked
import org.scalactic.Prettifier
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// ── test models (package-level to avoid path-dependent type issues with macros) ──

private[mockito] trait IA_Foo {
  def bar: String
  def baz(x: Int): String
  def unit(): Unit
}

/**
 * Cross-version unit tests for [[IdiomaticStubbing]] and [[PostfixVerifications]].
 *
 * Exercises the idiomatic stubbing and verification DSL (returns, answers, throws, willBe, was called, etc.) via [[IdiomaticMockito]], which picks up the version-specific
 * implementations at compile time.
 */
class IdiomaticApiTest extends AnyWordSpec with Matchers with IdiomaticMockito with ArgumentMatchersSugar {

  implicit val prettifier: Prettifier = Prettifier.default

  "IdiomaticStubbing" should {

    "stub a return value with returns" in {
      val m = mock[IA_Foo]
      m.bar returns "mocked"
      m.bar shouldBe "mocked"
    }

    "stub a return value with shouldReturn" in {
      val m = mock[IA_Foo]
      m.bar shouldReturn "mocked"
      m.bar shouldBe "mocked"
    }

    "stub multiple return values with returns andThen" in {
      val m = mock[IA_Foo]
      m.bar returns "first" andThen "second"
      m.bar shouldBe "first"
      m.bar shouldBe "second"
    }

    "stub a computed answer with answers" in {
      val m     = mock[IA_Foo]
      var count = 0
      m.bar answers { count += 1; count.toString }
      m.bar shouldBe "1"
      m.bar shouldBe "2"
    }

    "stub a one-arg answer with answers" in {
      val m = mock[IA_Foo]
      m.baz(*) answers ((x: Int) => s"got $x")
      m.baz(42) shouldBe "got 42"
    }

    "stub an exception to be thrown with throws" in {
      val m = mock[IA_Foo]
      m.bar throws new IllegalArgumentException("boom")
      an[IllegalArgumentException] shouldBe thrownBy(m.bar)
    }

    "stub doesNothing for unit method" in {
      val m = mock[IA_Foo]
      m.unit().doesNothing()
      noException shouldBe thrownBy(m.unit())
    }

    "stub a return value with willBe returned by" in {
      val m = mock[IA_Foo]
      "mocked" willBe returned by m.bar
      m.bar shouldBe "mocked"
    }

    "stub a computed answer with willBe answered by" in {
      val m                   = mock[IA_Foo]
      var count               = 0
      val thunk: () => String = () => { count += 1; count.toString }
      thunk willBe answered by m.bar
      m.bar shouldBe "1"
      m.bar shouldBe "2"
    }

    "stub an exception with willBe thrown by" in {
      val m = mock[IA_Foo]
      new IllegalArgumentException("boom") willBe thrown by m.bar
      an[IllegalArgumentException] shouldBe thrownBy(m.bar)
    }
  }

  "PostfixVerifications" should {

    "verify a method was called" in {
      val m = mock[IA_Foo]
      m.bar returns "x"
      m.bar
      m.bar was called
    }

    "verify a method was never called" in {
      val m = mock[IA_Foo]
      m.bar wasNever called
    }

    "verify a method was called once" in {
      val m = mock[IA_Foo]
      m.bar returns "x"
      m.bar
      m.bar wasCalled once
    }

    "verify a method was called twice" in {
      val m = mock[IA_Foo]
      m.bar returns "x"
      m.bar
      m.bar
      m.bar wasCalled twice
    }

    "fail verification when expected call was not made" in {
      val m = mock[IA_Foo]
      a[WantedButNotInvoked] shouldBe thrownBy {
        m.bar was called
      }
    }
  }
}
