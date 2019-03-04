package user.org.mockito.scalatest

import org.mockito.scalatest.ScalatestAsyncMockito
import org.scalatest.{AsyncWordSpec, FixtureContext, Matchers}

class ScalatestAsyncMockitoTest extends AsyncWordSpec with Matchers with ScalatestAsyncMockito {

  class Foo {
    def bar(a: String) = "bar"
  }

  trait Setup {
    val foo: Foo = mock[Foo]
  }

  "ScalatestAsyncMockito" should {
    "check the mocks were called with the right arguments" in {
      val foo = mock[Foo]

      foo.bar(*) shouldReturn "mocked"

      foo.bar("pepe") shouldBe "mocked"

      foo.bar("pepe") was called
    }

    "work on tests with setup" in new Setup with FixtureContext {
      "mocked" willBe returned by foo.bar("pepe")

      foo.bar("pepe") shouldBe "mocked"
    }
  }

}
