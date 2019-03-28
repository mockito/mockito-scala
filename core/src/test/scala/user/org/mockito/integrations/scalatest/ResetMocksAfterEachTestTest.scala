package user.org.mockito.integrations.scalatest

import org.mockito.MockitoSugar
import org.mockito.integrations.scalatest.ResetMocksAfterEachTest
import org.scalatest.{Matchers, WordSpec}

class ResetMocksAfterEachTestTest extends WordSpec with MockitoSugar with ResetMocksAfterEachTest with Matchers {

  trait Foo {
    def bar(a: String) = "bar"
  }

  trait Baz {
    def qux(a: String) = "qux"
  }

  val foo: Foo = mock[Foo]
  val baz: Baz = mock[Baz]

  "ResetMocksAfterEachTest" should {

    "have clean state for test 1" in {

      verifyZeroInteractions(foo)

      when(foo.bar("pepe")) thenReturn "mocked"

      foo.bar("pepe") shouldBe "mocked"
    }

    "have clean state for test 2" in {

      verifyZeroInteractions(foo)

      when(foo.bar("pepe")) thenReturn "mocked2"

      foo.bar("pepe") shouldBe "mocked2"
    }

    "have clean state for all mocks test 1" in {

      verifyZeroInteractions(foo, baz)

      when(foo.bar("pepe")) thenReturn "mocked3"
      when(baz.qux("epep")) thenReturn "mocked4"

      foo.bar("pepe") shouldBe "mocked3"
      baz.qux("epep") shouldBe "mocked4"
    }

    "have clean state for all mocks test 2" in {

      verifyZeroInteractions(foo, baz)

      when(foo.bar("pepe")) thenReturn "mocked5"
      when(baz.qux("epep")) thenReturn "mocked6"

      foo.bar("pepe") shouldBe "mocked5"
      baz.qux("epep") shouldBe "mocked6"
    }

  }

}
