package user.org.mockito

import org.mockito.MockitoSugar
import org.mockito.stubbing.{ CallsRealMethods, DefaultAnswer, ReturnsDefaults }
import org.scalatest.{ WordSpec, Matchers => ScalatestMatchers }

//noinspection RedundantDefaultArgument
class MockitoSugar$Test extends WordSpec with ScalatestMatchers {

  class Foo {
    def bar = "not mocked"
  }

  "mock[T]" should {
    "create a valid mock" in {
      val aMock = MockitoSugar.mock[Foo]

      MockitoSugar.when(aMock.bar) thenReturn "mocked!"

      aMock.bar shouldBe "mocked!"
    }

    "create a mock with default answer" in {
      val aMock = MockitoSugar.mock[Foo](CallsRealMethods)

      aMock.bar shouldBe "not mocked"
    }

    "create a mock with default answer from implicit scope" in {
      implicit val defaultAnswer: DefaultAnswer = CallsRealMethods

      val aMock = MockitoSugar.mock[Foo]

      aMock.bar shouldBe "not mocked"
    }

    "create a mock with name" in {
      val aMock = MockitoSugar.mock[Foo]("Nice Mock")

      aMock.toString shouldBe "Nice Mock"
    }
  }
}
