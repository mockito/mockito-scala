package user.org.mockito.scalatest

import org.mockito.scalatest.ScalatestMockito
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{AsyncWordSpec, Matchers}
import user.org.mockito.Org

//TODO check why it doesn't work with mockito fixture
class ScalatestAsyncCompatibilityTest extends AsyncWordSpec with Matchers with ScalatestMockito with TableDrivenPropertyChecks {

  val scenarios = Table(
    ("testDouble", "orgDouble"),
    ("mock", () => mock[Org]),
    ("spy", () => spy(new Org))
  )

  forAll(scenarios) { (testDouble, orgDouble) =>
    testDouble should {
      "stub a return value" in {
        val org = orgDouble()

        org.bar shouldReturn "mocked!"

        org.bar shouldBe "mocked!"

        org.bar was called
      }
    }
  }

}
