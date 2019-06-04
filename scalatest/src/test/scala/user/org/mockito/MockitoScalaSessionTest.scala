package user.org.mockito

import org.mockito._
import org.mockito.exceptions.misusing.{ UnexpectedInvocationException, UnnecessaryStubbingException }
import org.mockito.exceptions.verification.SmartNullPointerException
import org.mockito.internal.creation.settings.CreationSettings
import org.mockito.quality.Strictness
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{ Matchers, OptionValues, WordSpec }

//noinspection RedundantDefaultArgument
class MockitoScalaSessionTest
    extends WordSpec
    with IdiomaticMockito
    with Matchers
    with ArgumentMatchersSugar
    with OptionValues
    with TableDrivenPropertyChecks {

  val scenarios = Table(
    ("testDouble", "foo", "parametrisedFoo", "fooBar"),
    ("mock", () => mock[Foo], (mockSettings: MockSettings) => mock[Foo](mockSettings), ""),
    ("spy",
     () => spy(new Foo),
     (mockSettings: MockSettings) => spy(new Foo, mockSettings.asInstanceOf[CreationSettings[_]].isLenient),
     "bar")
  )

  class Foo {
    def bar(a: String) = "bar"

    def baz(a: String = "default"): String = a

    def userClass: Bar = new Bar

    def userClassFinal: BarFinal = new BarFinal
  }

  class Bar {
    def callMeMaybe: Baz = new Baz
    def dontCallMe: Baz  = new Baz
  }

  class Baz {
    def callMe: Option[String]     = None
    def dontCallMe: Option[String] = None
  }

  final class BarFinal {
    def callMeMaybe: Option[Boolean] = None
  }

  forAll(scenarios) { (testDouble, foo, parametrisedFoo, fooBar) =>
    s"MockitoScalaSession - $testDouble" should {

      "don't check unexpected calls for lenient methods (set at the beginning)" in {
        MockitoScalaSession().run {
          val aFoo = foo()

          aFoo.bar(*).isLenient()

          aFoo.bar("paco") shouldBe fooBar
        }
      }

      "don't check unexpected calls for lenient methods (set at the end)" in {
        MockitoScalaSession().run {
          val aFoo = foo()

          aFoo.bar("paco") shouldBe fooBar

          aFoo.bar(*).isLenient()
        }
      }

      "don't check unused stubs for lenient methods (set at the beginning)" in {
        MockitoScalaSession().run {
          val aFoo = foo()

          aFoo.bar(*).isLenient()

          aFoo.bar("pepe") returns "mocked"
        }
      }

      "don't check unused stubs for lenient methods (set at the end)" in {
        MockitoScalaSession().run {
          val aFoo = foo()

          aFoo.bar("pepe") returns "mocked"

          aFoo.bar(*).isLenient()
        }
      }

      "check unused stubs" in {
        an[UnnecessaryStubbingException] should be thrownBy {
          MockitoScalaSession().run {
            val aFoo = foo()

            aFoo.bar(*) returns "mocked"
          }
        }
      }

      "check incorrect stubs" in {
        an[UnnecessaryStubbingException] should be thrownBy {
          MockitoScalaSession().run {
            val aFoo = foo()

            aFoo.bar("pepe") returns "mocked"

            aFoo.bar("paco").toLowerCase
          }
        }
      }

      "check incorrect stubs after the expected one was called" in {
        val thrown = the[UnexpectedInvocationException] thrownBy {
          MockitoScalaSession().run {
            val aFoo = foo()

            aFoo.bar("pepe") returns "mocked"

            aFoo.bar("pepe")

            aFoo.bar("paco").toLowerCase
          }
        }

        thrown.getMessage should startWith("Unexpected invocations found")
      }

      "check unexpected invocations" in {
        val thrown = the[UnexpectedInvocationException] thrownBy {
          MockitoScalaSession().run {
            val aFoo = foo()

            aFoo.bar("pepe")
          }
        }

        thrown.getMessage should startWith("Unexpected invocations found")
      }

      "not check unexpected invocations if the call was verified" in {
        MockitoScalaSession().run {
          val aFoo = foo()

          aFoo.bar("pepe")

          aFoo.bar("pepe") was called
        }
      }

      "check incorrect stubs with default arguments" in {
        an[UnnecessaryStubbingException] should be thrownBy {
          MockitoScalaSession().run {
            val aFoo = foo()

            aFoo.baz("pepe") returns "mocked"

            aFoo.baz().toLowerCase
          }
        }
      }

      "work with default arguments" in {
        MockitoScalaSession().run {
          val aFoo = foo()

          aFoo.baz() returns "mocked"

          aFoo.baz() shouldBe "mocked"
        }
      }

      "work with default arguments when passing an argument" in {
        MockitoScalaSession().run {
          val aFoo = foo()

          aFoo.baz("papa") returns "mocked"

          aFoo.baz("papa") shouldBe "mocked"
        }
      }

      "work with default arguments when passing an argument but production code doesn't" in {
        MockitoScalaSession().run {
          val aFoo = foo()

          aFoo.baz("default") returns "mocked"

          aFoo.baz() shouldBe "mocked"
        }
      }

      "don't check unexpected calls for lenient mocks" in {
        MockitoScalaSession().run {
          val aFoo = parametrisedFoo(withSettings.lenient())

          aFoo.bar("pepe") returns "mocked"

          aFoo.bar("pepe")

          aFoo.bar("paco")
        }
      }

      "check unexpected invocations for normal mocks" in {
        intercept[UnexpectedInvocationException] {
          MockitoScalaSession().run {
            val aFoo = foo()

            aFoo.bar("pepe") returns "mocked"

            aFoo.bar("pepe")

            aFoo.bar("paco")
          }
        }
      }

      "don't check unexpected invocations in lenient setting" in {
        MockitoScalaSession(strictness = Strictness.LENIENT).run {
          val aFoo = foo()

          aFoo.bar("pepe") returns "mocked"

          aFoo.bar("pepe")

          aFoo.bar("paco")
        }
      }

      "don't check unused stubs for lenient" in {
        MockitoScalaSession().run {
          val aFoo = parametrisedFoo(withSettings.lenient())

          aFoo.bar("pepe") returns "mocked"
        }
      }

      "check unused stubs for not lenient mocks" in {
        intercept[UnnecessaryStubbingException] {
          MockitoScalaSession().run {
            val aFoo = foo()
            aFoo.bar("pepe") returns "mocked"
          }
        }
      }

      "don't check unused stubs in lenient setting" in {
        MockitoScalaSession(strictness = Strictness.LENIENT).run {
          val aFoo = foo()
          aFoo.bar("pepe") returns "mocked"
        }
      }

      "check unused stubs in not lenient setting" in {
        intercept[UnnecessaryStubbingException] {
          MockitoScalaSession(strictness = Strictness.STRICT_STUBS).run {
            val aFoo = foo()
            aFoo.bar("pepe") returns "mocked"
          }
        }
      }
    }
  }

  "MockitoScalaSession" should {
    "re-throw an exception produced by the test" in {
      an[IllegalArgumentException] should be thrownBy {
        MockitoScalaSession().run {
          throw new IllegalArgumentException
        }
      }
    }

    "re-throw an real NPE produced by the test (an NPE not related to an un-stubbed mock call)" in {
      an[NullPointerException] should be thrownBy {
        MockitoScalaSession().run {
          throw new NullPointerException
        }
      }
    }

    "work with nested deep stubs" in {
      MockitoScalaSession().run {
        val aFoo = mock[Foo](DefaultAnswers.ReturnsDeepStubs)

        aFoo.userClass.callMeMaybe.callMe returns Some("my number")

        aFoo.userClass.callMeMaybe.callMe.value shouldBe "my number"
      }
    }

    "not fail if a final deep stub is called in a non stubbed method" in {
      MockitoScalaSession().run {
        val aFoo = mock[Foo](DefaultAnswers.ReturnsDeepStubs)

        aFoo.userClass.callMeMaybe.callMe returns Some("my number")

        aFoo.userClass.callMeMaybe.callMe.value shouldBe "my number"

        aFoo.userClass.callMeMaybe.dontCallMe

      }
    }

    "not fail if a nested deep stub is called in a non stubbed method" in {
      MockitoScalaSession().run {
        val aFoo = mock[Foo](DefaultAnswers.ReturnsDeepStubs)

        aFoo.userClass.callMeMaybe.callMe returns Some("my number")

        aFoo.userClass.callMeMaybe.callMe.value shouldBe "my number"

        aFoo.userClass.dontCallMe

      }
    }

    "fail if a nested deep stub is stubbed but not used" in {
      val thrown = the[UnnecessaryStubbingException] thrownBy {
        MockitoScalaSession().run {
          val aFoo = mock[Foo](DefaultAnswers.ReturnsDeepStubs)

          aFoo.userClass.callMeMaybe.callMe returns Some("my number")

        }
      }

      thrown.getMessage should startWith("Unnecessary stubbings detected")
    }

    "check incorrect stubs after the expected one was called on a final class" in {
      val thrown = the[UnexpectedInvocationException] thrownBy {
        MockitoScalaSession().run {
          val aFoo = mock[Foo]

          aFoo.userClassFinal.callMeMaybe
        }
      }

      thrown.getMessage should startWith("A NullPointerException was thrown, check if maybe related to")
    }

    "check SmartNull" in {
      val thrown = the[SmartNullPointerException] thrownBy {
        MockitoScalaSession().run {
          val aFoo = mock[Foo]

          aFoo.userClass.callMeMaybe
        }
      }

      thrown.getMessage should include("You have a NullPointerException here:")
    }
  }
}
