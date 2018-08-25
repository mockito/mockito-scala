package org.mockito

import org.scalatest
import org.mockito.exceptions.misusing.{PotentialStubbingProblem, UnexpectedInvocationException, UnnecessaryStubbingException}
import org.mockito.exceptions.verification.SmartNullPointerException
import org.scalatest.WordSpec

//noinspection RedundantDefaultArgument
class MockitoScalaSessionTest extends WordSpec with IdiomaticMockito with scalatest.Matchers {

  class Foo {
    def bar(a: String) = "bar"

    def baz(a: String = "default"): String = a

    def userClass: Bar = new Bar

    def userClassFinal: BarFinal = new BarFinal
  }

  class Bar {
    def callMeMaybe: Option[Boolean] = None
  }

  final class BarFinal {
    def callMeMaybe: Option[Boolean] = None
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

      thrown.getMessage should startWith("A NullPointerException was thrown, check if maybe related to")
    }

    "check SmartNull" in {
      val thrown = the[SmartNullPointerException] thrownBy {
        MockitoScalaSession().run {
          val foo = mock[Foo]

          foo.userClass.callMeMaybe
        }
      }

      thrown.getMessage should startWith("You have a NullPointerException because this method call was *not* stubbed correctly")
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

        foo wasCalled on bar "pepe"
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
  }

}
