package org.mockito.integrations.scalatest

import org.scalatest.{Matchers, WordSpec}

class IdiomaticMockitoFixtureTest extends WordSpec with IdiomaticMockitoFixture with Matchers {

  class Foo {
    def bar(a: String) = "bar"
  }

  trait Setup {
    val foo: Foo = mock[Foo]
  }

  "MockitoFixture" should {
    "check the mocks were called with the right arguments" in {
      val foo = mock[Foo]

      foo.bar(*) shouldReturn "mocked"

      foo.bar("pepe") shouldBe "mocked"
    }

    "work on tests with setup" in new Setup {
      "mocked" willBe returned by foo bar "pepe"

      foo.bar("pepe") shouldBe "mocked"
    }
  }

}
