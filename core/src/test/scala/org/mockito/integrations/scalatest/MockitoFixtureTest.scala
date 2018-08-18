package org.mockito.integrations.scalatest

import org.scalatest.{ Matchers, WordSpec }

class MockitoFixtureTest extends WordSpec with MockitoFixture with Matchers {

  class Foo {
    def bar(a: String) = "bar"
    def baz(a: String = "default") = a
  }

  trait Setup {
    val foo = mock[Foo]
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

    "work with default arguments" in new Setup {
      when(foo.baz()) thenReturn "mocked"
      foo.baz() shouldBe "mocked"
    }

    "work with default arguments when passing an argument" in new Setup {
      when(foo.baz("papa")) thenReturn "mocked"
      foo.baz("papa") shouldBe "mocked"
    }

    "work with default arguments when passing an argument but production code doesn't" in new Setup {
      when(foo.baz("default")) thenReturn "mocked"
      foo.baz() shouldBe "mocked"
    }
  }

}
