package user.org.mockito.scalatest

import org.mockito.scalatest.AsyncIdiomaticMockito

import scala.concurrent.Future
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class AsyncIdiomaticMockitoTest_213 extends AsyncWordSpec with Matchers with AsyncIdiomaticMockito {
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
