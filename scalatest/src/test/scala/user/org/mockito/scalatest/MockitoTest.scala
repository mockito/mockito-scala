package user.org.mockito.scalatest

import org.mockito.scalatest.Mockito
import org.scalatest.{ Matchers, WordSpec }

class MockitoTest extends WordSpec with Mockito with Matchers {

  class Foo {
    def bar(a: String) = "bar"
  }

  trait Setup {
    val foo: Foo = mock[Foo]
  }

  "ScalatestMockito" should {
    "check the mocks were called with the right arguments" in {
      val foo = mock[Foo]

      foo.bar(*) shouldReturn "mocked"

      foo.bar("pepe") shouldBe "mocked"
    }

    "work on tests with setup" in new Setup {
      "mocked" willBe returned by foo.bar("pepe")

      foo.bar("pepe") shouldBe "mocked"
    }
  }

}
