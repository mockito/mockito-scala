package user.org.mockito.captor

import org.mockito.captor.ArgCaptor
import org.mockito.exceptions.base.MockitoAssertionError
import org.mockito.exceptions.verification.{ TooFewActualInvocations, TooManyActualInvocations }
import org.mockito.{ IdiomaticMockito, MockitoSugar }
import org.scalactic.{ Equality, StringNormalizations }
import user.org.mockito.captor.ArgCaptorTest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

object ArgCaptorTest {
  case class Name(name: String) extends AnyVal

  class Email(val email: String) extends AnyVal

  class Foo {
    def stringArgument(s: String): String = s

    def intArgument(i: Int): Int = ???

    def complexArgument(m: Map[String, Int]): Map[String, Int] = ???

    def valueCaseClass(name: Name): String = ???

    def valueClass(email: Email): String = ???

    def valueClassAndValue(email: Email, s: String): String = ???
  }
}

class ArgCaptorTest extends AnyWordSpec with MockitoSugar with Matchers {
  "Captor" should {
    "capture a simple AnyRef argument" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[String]

      aMock.stringArgument("it worked!")

      verify(aMock).stringArgument(captor)

      captor hasCaptured "it worked!"
    }

    "works with Equality" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[String]
      import StringNormalizations._

      implicit val eq: Equality[String] = decided by defaultEquality[String] afterBeing lowerCased

      aMock.stringArgument("It Worked!")

      verify(aMock).stringArgument(captor)

      captor hasCaptured "it worked!"
    }

    "capture a simple AnyVal argument" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[Int]

      aMock.intArgument(42)

      verify(aMock).intArgument(captor)

      captor hasCaptured 42
    }

    "capture a complex argument" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[Map[String, Int]]

      aMock.complexArgument(Map("Works" -> 1))

      verify(aMock).complexArgument(captor)

      captor hasCaptured Map("Works" -> 1)
    }

    "expose the captured value to use with custom matchers" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[String]

      aMock.stringArgument("it worked!")

      verify(aMock).stringArgument(captor)

      captor.value shouldBe "it worked!"
    }

    "expose all the captured values to use with custom matchers" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[String]

      aMock.stringArgument("it worked!")
      aMock.stringArgument("it worked again!")

      verify(aMock, times(2)).stringArgument(captor)

      captor.values.should(contain).only("it worked!", "it worked again!")
      captor.hasCaptured("it worked!", "it worked again!")
    }

    "report failure" when {

      "fewer values were captured than expected" in {
        val aMock  = mock[Foo]
        val captor = ArgCaptor[String]

        aMock.stringArgument("it worked!")
        aMock.stringArgument("it worked again!")

        verify(aMock, times(2)).stringArgument(captor)

        captor.values.should(contain).only("it worked!", "it worked again!")

        the[TooManyActualInvocations] thrownBy {
          captor.hasCaptured("it worked!")
        } should have message "Also got 1 more: [it worked again!]"
      }

      "more values were captured than expected" in {
        val aMock  = mock[Foo]
        val captor = ArgCaptor[String]

        aMock.stringArgument("it worked!")

        verify(aMock, times(1)).stringArgument(captor)

        captor.values.should(contain).only("it worked!")

        the[TooFewActualInvocations] thrownBy {
          captor.hasCaptured("it worked!", "it worked again!")
        } should have message "Also expected 1 more: [it worked again!]"
      }

      "fewer values were captured than expected while wrong values were captured" in {
        val aMock  = mock[Foo]
        val captor = ArgCaptor[String]

        aMock.stringArgument("it worked again!")

        verify(aMock, times(1)).stringArgument(captor)

        captor.values.should(contain).only("it worked again!")

        val error = the[MockitoAssertionError] thrownBy {
          captor.hasCaptured("it worked!", "it worked again!")
        }

        error.getMessage should (
          include("Got [it worked again!] instead of [it worked!]") and
          include("Also expected 1 more: [it worked again!]")
        )
      }

      "more values were captured than expected while wrong values were captured" in {
        val aMock  = mock[Foo]
        val captor = ArgCaptor[String]

        aMock.stringArgument("it worked!")
        aMock.stringArgument("it worked again!")

        verify(aMock, times(2)).stringArgument(captor)

        captor.values.should(contain).only("it worked!", "it worked again!")

        val error = the[MockitoAssertionError] thrownBy {
          captor.hasCaptured("it worked again!")
        }

        error.getMessage should (
          include("Got [it worked!] instead of [it worked again!]") and
          include("Also got 1 more: [it worked again!]")
        )
      }
    }

    "work with value case classes" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[Name]

      aMock.valueCaseClass(Name("Batman"))

      verify(aMock).valueCaseClass(captor)

      captor hasCaptured Name("Batman")
      captor.value shouldBe Name("Batman")
      captor.values should contain only Name("Batman")
    }

    "work with value non-case classes" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[Email]

      aMock.valueClass(new Email("batman@batcave.gotham"))

      verify(aMock).valueClass(captor)

      captor hasCaptured new Email("batman@batcave.gotham")
      captor.value shouldBe new Email("batman@batcave.gotham")
      captor.values should contain only new Email("batman@batcave.gotham")
    }

    "work with mixture of value class & value param" in new IdiomaticMockito {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[Email]

      aMock.valueClassAndValue(new Email("batman@batcave.gotham"), "42")

      aMock.valueClassAndValue(captor, "42") was called

      captor hasCaptured new Email("batman@batcave.gotham")
      captor.value shouldBe new Email("batman@batcave.gotham")
      captor.values should contain only new Email("batman@batcave.gotham")
    }
  }
}
