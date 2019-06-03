package user.org.mockito.scalatest

import org.mockito.scalatest.AsyncIdiomaticMockito
import org.scalatest.{ AsyncWordSpec, Matchers }

import scala.concurrent.Future

class AsyncIdiomaticMockitoTest extends AsyncWordSpec with Matchers with AsyncIdiomaticMockito {

  class Foo {
    def bar(a: String) = "bar"
  }

  class Baz {
    def fut(f: Foo) = Future {
      Thread.sleep(500)
      f.bar("in the future")
    }
  }

  class Setup {
    val foo: Foo = mock[Foo]
  }

  "AsyncMockito" should {
    "check the mocks were called with the right arguments" in {
      val foo = mock[Foo]

      foo.bar(*) returns "mocked"

      foo.bar("pepe") shouldBe "mocked"

      foo.bar("pepe") was called
    }

    "work on tests with setup" in {
      val setup = new Setup
      import setup._

      "mocked" willBe returned by foo.bar("pepe")

      foo.bar("pepe") shouldBe "mocked"

      foo.bar("pepe") was called
    }

    "work with real future assertions" in {
      val setup = new Setup
      import setup._

      val baz = new Baz

      foo.bar(*) returns "mocked"

      baz.fut(foo).map { r =>
        r shouldBe "mocked"
        foo.bar("in the future") was called
      }
    }
  }
}
