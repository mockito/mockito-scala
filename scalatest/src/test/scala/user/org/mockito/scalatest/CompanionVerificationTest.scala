package user.org.mockito.scalatest

import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * Regression test for VerifyMacro.findVerificationSymbol companion module lookup.
 *
 * Pattern: a companion object extends [[org.mockito.scalatest.IdiomaticMockito]] (which provides `verification`), while the test class itself extends some scalatest trait. In
 * Scala 2 the quasiquote `q"verification(...)"` resolves the name naturally via normal scoping rules, which includes companion objects. In Scala 3 the macro builds the AST
 * explicitly, so it must walk the owner chain AND check companion modules at each level; otherwise `was called` / `wasNever called` fail at compile time.
 */
object CompanionVerificationTest extends IdiomaticMockito with Matchers

class CompanionVerificationTest extends AnyWordSpec with Matchers {
  import CompanionVerificationTest.*

  class Foo {
    def bar(a: String): String = "not mocked"
    def baz(): Int             = -1
  }

  "verification via companion object" should {
    "find verification on was called" in {
      val foo = mock[Foo]

      foo.bar(*) returns "mocked"
      foo.bar("pepe") shouldBe "mocked"

      foo.bar("pepe") was called
    }

    "find verification on wasNever called" in {
      val foo = mock[Foo]

      foo.baz() wasNever called
    }

  }
}
