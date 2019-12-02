package user.org.mockito

import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Test provided in https://github.com/mockito/mockito-scala/issues/171
 */
class NestedReproSpec extends AnyFlatSpec with Matchers with IdiomaticMockito with ArgumentMatchersSugar {
  val exampler = mock[Exampler]

  it should "compile a nested value class with direct symbolic reference" in {
    """
      |import Nesting.NestedValueClass
      |exampler(any[NestedValueClass]) returns ???
      |""".stripMargin should compile
  }

  it should "compile a nested value class with reference to enclosing object" in {
    "exampler(any[Nesting.NestedValueClass]) returns ???" should compile
  }
}

object Nesting {
  case class NestedValueClass(value: String) extends AnyVal
}

trait Exampler {
  def apply(v: Nesting.NestedValueClass): String
}
