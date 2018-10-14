package user.org.mockito.integrations.scalatest

import org.mockito.MockitoSugar
import org.mockito.integrations.scalatest.ResetMocksAfterEachTest
import org.scalatest.{Matchers, WordSpec}

class ResetMocksAfterEachTestTest extends WordSpec with MockitoSugar with ResetMocksAfterEachTest with Matchers {

  trait Foo {
    def bar(a: String) = "bar"
  }

  val foo = mock[Foo]

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

  }

}
