package user.org.mockito.scalatest

import org.mockito.scalatest.AsyncIdiomaticMockito
import org.scalatest.{ AsyncWordSpec, Matchers }

import scala.concurrent.Future

class AsyncIdiomaticMockitoTest_212 extends AsyncWordSpec with Matchers with AsyncIdiomaticMockito {

  "AsyncMockito" should {
    "work with specialised methods" in {
      val mockFunction = mock[() => Int]
      mockFunction() returns 42

      Future(mockFunction.apply())
        .map { v =>
          v shouldBe 42
          mockFunction() was called
        }
    }
  }
}
