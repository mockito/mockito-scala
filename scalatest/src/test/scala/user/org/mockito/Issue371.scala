package user.org.mockito

import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant

class Issue371 extends AnyFlatSpec with IdiomaticMockito with Matchers with ArgumentMatchersSugar {

  trait TestTrait {
    def anyRef(a: Instant, b: Instant): Unit
  }

  it should "deal with matchers on named params" in {
    val t = mock[TestTrait]

    t.anyRef(Instant.EPOCH, Instant.EPOCH)

    t.anyRef(b = Instant.EPOCH, a = Instant.EPOCH).wasCalled(once)
  }

}
