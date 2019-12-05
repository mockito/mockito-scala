package user.org.mockito.scalatest

import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class IdiomaticMockitoTest extends AnyWordSpec with IdiomaticMockito with Matchers {
  class Foo {
    def bar(a: String) = "bar"
  }

  trait Setup {
    val foo: Foo = mock[Foo]
  }

  "Mockito" should {
    "check the mocks were called with the right arguments" in {
      val foo = mock[Foo]

      foo.bar(*) returns "mocked"

      foo.bar("pepe") shouldBe "mocked"

      foo.bar("pepe") was called
    }

    "work on tests with setup" in new Setup {
      "mocked" willBe returned by foo.bar("pepe")

      foo.bar("pepe") shouldBe "mocked"

      foo.bar("pepe") was called
    }
  }
}
