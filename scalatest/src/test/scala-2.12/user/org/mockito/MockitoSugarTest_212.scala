package user.org.mockito

import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{ Matchers, WordSpec }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//noinspection RedundantDefaultArgument
class MockitoSugarTest_212 extends WordSpec with MockitoSugar with Matchers with ArgumentMatchersSugar with TableDrivenPropertyChecks with ScalaFutures {
  val scenarios = Table(
    ("testDouble", "foo", "baz"),
    ("mock", () => mock[Foo], () => mock[Baz]),
    ("spy", () => spy(new Foo), () => spy(new ConcreteBaz))
  )

  forAll(scenarios) { (testDouble, foo, baz) =>
    testDouble should {
      "work with default arguments in traits" in {
        val testDouble = baz()

        when(testDouble.traitMethodWithDefaultArgs(any, any)) thenReturn 69

        testDouble.traitMethodWithDefaultArgs() shouldBe 69

        verify(testDouble).traitMethodWithDefaultArgs(30, "hola")
      }

      "work with by-name arguments and matchers (by-name arguments have to be the last ones when using matchers)" in {
        val testDouble = foo()

        when(testDouble.iHaveByNameArgs(any, any, any)) thenReturn "mocked!"

        testDouble.iHaveByNameArgs("arg1", "arg2", "arg3") shouldBe "mocked!"

        verify(testDouble).iHaveByNameArgs(eqTo("arg1"), endsWith("g2"), eqTo("arg3"))
      }

      "work with by-name and Function0 arguments (by-name arguments have to be the last ones when using matchers)" in {
        val testDouble = foo()

        when(testDouble.iHaveByNameAndFunction0Args(any, any, any)) thenReturn "mocked!"

        testDouble.iHaveByNameAndFunction0Args("arg1", () => "arg2", "arg3") shouldBe "mocked!"

        verify(testDouble).iHaveByNameAndFunction0Args(eqTo("arg1"), function0("arg2"), startsWith("arg"))
      }
    }
  }

  "mock[T]" should {
    "work with specialised methods" in {
      val mockFunction = mock[() => Unit]

      val f = Future(mockFunction.apply())

      whenReady(f)(_ => verify(mockFunction).apply())
    }
  }
}
