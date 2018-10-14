package user.org.mockito.integrations.scalatest

import org.mockito.integrations.scalatest.MockitoFixture
import org.scalatest.{Matchers, WordSpec}

class MockitoFixtureTest extends WordSpec with MockitoFixture with Matchers {

  class Foo {
    def bar(a: String) = "bar"
  }

  trait Setup {
    val foo: Foo = mock[Foo]
  }

  "MockitoFixture" should {
    "check the mocks were called with the right arguments" in {
      val foo = mock[Foo]
      when(foo.bar("pepe")) thenReturn "mocked"
      foo.bar("pepe") shouldBe "mocked"
    }

    "work on tests with setup" in new Setup {
      doReturn("mocked").when(foo).bar("pepe")
      foo.bar("pepe") shouldBe "mocked"
    }
  }

}
