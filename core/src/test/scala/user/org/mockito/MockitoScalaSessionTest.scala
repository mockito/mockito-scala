package user.org.mockito

import org.mockito.exceptions.misusing.{PotentialStubbingProblem, UnexpectedInvocationException, UnnecessaryStubbingException}
import org.mockito.exceptions.verification.SmartNullPointerException
import org.mockito.quality.Strictness
import org.mockito.{ArgumentMatchersSugar, DefaultAnswers, IdiomaticMockito, MockitoScalaSession}
import org.scalatest
import org.scalatest.{OptionValues, WordSpec}

//noinspection RedundantDefaultArgument
class MockitoScalaSessionTest extends WordSpec with IdiomaticMockito with scalatest.Matchers with ArgumentMatchersSugar with OptionValues {

  class Foo {
    def bar(a: String) = "bar"

    def baz(a: String = "default"): String = a

    def userClass: Bar = new Bar

    def userClassFinal: BarFinal = new BarFinal
  }

  class Bar {
    def callMeMaybe: Baz = ???
    def dontCallMe: Baz  = ???
  }

  class Baz {
    def callMe: Option[String]     = ???
    def dontCallMe: Option[String] = ???
  }

  final class BarFinal {
    def callMeMaybe: Option[Boolean] = ???
  }

  "MockitoScalaSession" should {
    "check unused stubs" in {
      an[UnnecessaryStubbingException] should be thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.bar(*) shouldReturn "mocked"
        }
      }
    }

    "check incorrect stubs" in {
      an[PotentialStubbingProblem] should be thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.bar("pepe") shouldReturn "mocked"

          foo.bar("paco").toLowerCase
        }
      }
    }

    "check incorrect stubs after the expected one was called" in {
      val thrown = the[UnexpectedInvocationException] thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.bar("pepe") shouldReturn "mocked"

          foo.bar("pepe")

          foo.bar("paco").toLowerCase
        }
      }

      thrown.getMessage should startWith("Unexpected invocations found")
    }

    "check SmartNull" in {
      val thrown = the[SmartNullPointerException] thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.userClass.callMeMaybe
        }
      }

      thrown.getMessage should include("You have a NullPointerException here:")
    }

    "check incorrect stubs after the expected one was called on a final class" in {
      val thrown = the[UnexpectedInvocationException] thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.userClassFinal.callMeMaybe
        }
      }

      thrown.getMessage should startWith("A NullPointerException was thrown, check if maybe related to")
    }

    "check unexpected invocations" in {
      val thrown = the[UnexpectedInvocationException] thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.bar("pepe")
        }
      }

      thrown.getMessage should startWith("Unexpected invocations found")
    }

    "not check unexpected invocations if the call was verified" in {
      MockitoScalaSession().run {
        val foo = mock[Foo]

        foo.bar("pepe")

        foo.bar("pepe") was called
      }
    }

    "check incorrect stubs with default arguments" in {
      an[PotentialStubbingProblem] should be thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.baz("pepe") shouldReturn "mocked"

          foo.baz().toLowerCase
        }
      }
    }

    "work with default arguments" in {
      MockitoScalaSession().run {
        val foo = mock[Foo]

        foo.baz() shouldReturn "mocked"

        foo.baz() shouldBe "mocked"
      }
    }

    "work with default arguments when passing an argument" in {
      MockitoScalaSession().run {
        val foo = mock[Foo]

        foo.baz("papa") shouldReturn "mocked"

        foo.baz("papa") shouldBe "mocked"
      }
    }

    "work with default arguments when passing an argument but production code doesn't" in {
      MockitoScalaSession().run {
        val foo = mock[Foo]

        foo.baz("default") shouldReturn "mocked"

        foo.baz() shouldBe "mocked"
      }
    }

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

    "don't check unexpected stubs for lenient mocks" in {
      MockitoScalaSession().run {
        val foo = mock[Foo](withSettings.lenient())

        foo.bar("pepe") shouldReturn "mocked"

        foo.bar("pepe")

        foo.bar("paco")
      }
    }
    "check unexpected stubs for lenient mocks" in {
      intercept[UnexpectedInvocationException] {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.bar("pepe") shouldReturn "mocked"

          foo.bar("pepe")

          foo.bar("paco")
        }
      }
    }

    "don't check unexpected stubs in lenient setting" in {
      MockitoScalaSession(strictness = Strictness.LENIENT).run {
        val foo = mock[Foo]

        foo.bar("pepe") shouldReturn "mocked"

        foo.bar("pepe")

        foo.bar("paco")
      }
    }
    "check unexpected stubs in lenient setting" in {
      intercept[UnexpectedInvocationException] {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.bar("pepe") shouldReturn "mocked"

          foo.bar("pepe")

          foo.bar("paco")
        }
      }
    }

    "don't check unused stubs for lenient mocks" in {
      MockitoScalaSession().run {
        val foo = mock[Foo](withSettings.lenient())
        foo.bar("pepe") shouldReturn "mocked"
      }
    }

    "check unused stubs for not lenient mocks" in {
      intercept[UnnecessaryStubbingException] {
        MockitoScalaSession().run {
          val foo = mock[Foo]
          foo.bar("pepe") shouldReturn "mocked"
        }
      }
    }

    "don't check unused stubs in lenient setting" in {
      MockitoScalaSession(strictness = Strictness.LENIENT).run {
        val foo = mock[Foo]
        foo.bar("pepe") shouldReturn "mocked"
      }
    }

    "check unused stubs in not lenient setting" in {
      intercept[UnnecessaryStubbingException] {
        MockitoScalaSession(strictness = Strictness.STRICT_STUBS).run {
          val foo = mock[Foo]
          foo.bar("pepe") shouldReturn "mocked"
        }
      }
    }
    "work with nested deep stubs" in {
      MockitoScalaSession().run {
        val foo = mock[Foo](DefaultAnswers.ReturnsDeepStubs)

        foo.userClass.callMeMaybe.callMe shouldReturn Some("my number")

        foo.userClass.callMeMaybe.callMe.value shouldBe "my number"
      }
    }

    "not fail if a final deep stub is called in a non stubbed method" in {
      MockitoScalaSession().run {
        val foo = mock[Foo](DefaultAnswers.ReturnsDeepStubs)

        foo.userClass.callMeMaybe.callMe shouldReturn Some("my number")

        foo.userClass.callMeMaybe.callMe.value shouldBe "my number"

        foo.userClass.callMeMaybe.dontCallMe

      }
    }

    "not fail if a nested deep stub is called in a non stubbed method" in {
      MockitoScalaSession().run {
        val foo = mock[Foo](DefaultAnswers.ReturnsDeepStubs)

        foo.userClass.callMeMaybe.callMe shouldReturn Some("my number")

        foo.userClass.callMeMaybe.callMe.value shouldBe "my number"

        foo.userClass.dontCallMe

      }
    }

    "fail if a nested deep stub is stubbed but not used" in {
      val thrown = the[UnnecessaryStubbingException] thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo](DefaultAnswers.ReturnsDeepStubs)

          foo.userClass.callMeMaybe.callMe shouldReturn Some("my number")

        }
      }

      thrown.getMessage should startWith("Unnecessary stubbings detected")
    }
  }

}
