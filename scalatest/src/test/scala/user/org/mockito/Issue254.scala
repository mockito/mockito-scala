package user.org.mockito

import org.mockito.scalatest.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Issue254 extends AnyFlatSpec with MockitoSugar with Matchers {

  class SomeClassToMock {
    def methodReturningAny(input: String): Any = None
  }

  "mocking method returning Any" should "not explode" in {
    val myMock = mock[SomeClassToMock]

    // will explode with
    // java.lang.ClassNotFoundException: scala.Any
    // because it tries to instatiate Any at org/mockito/ReflectionUtils.scala:57
    when(myMock.methodReturningAny("test"))
      .thenReturn("some value")

    myMock.methodReturningAny("test") shouldBe "some value"
  }
}
