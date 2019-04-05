package user.org.mockito.scalatest

import org.mockito.scalatest.AsyncMockito
import org.scalatest.{ AsyncWordSpec, FixtureContext, Matchers }

import scala.concurrent.Future

class AsyncMockitoTest extends AsyncWordSpec with Matchers with AsyncMockito {

  class Foo {
    def bar(a: String) = "bar"
  }

  class Baz {
    def fut(f: Foo) = Future {
      Thread.sleep(500)
      f.bar("in the future")
    }
  }

  trait Setup {
    val foo: Foo = mock[Foo]
  }

  "AsyncMockito" should {
    "check the mocks were called with the right arguments" in {
      val foo = mock[Foo]

      foo.bar(*) shouldReturn "mocked"

      foo.bar("pepe") shouldBe "mocked"

      foo.bar("pepe") was called
    }

    "work on tests with setup" in new Setup with FixtureContext {
      "mocked" willBe returned by foo.bar("pepe")

      foo.bar("pepe") shouldBe "mocked"

      foo.bar("pepe") was called
    }

    "work with real future assertions" in {
      val foo = mock[Foo]
      val baz = new Baz

      foo.bar(*) shouldReturn "mocked"

      baz.fut(foo).map { r =>
        r shouldBe "mocked"
        foo.bar("in the future") was called
      }
    }
  }

}
