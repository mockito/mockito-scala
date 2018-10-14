package user.org.mockito.captor

import org.mockito.captor.{ArgCaptor, ValCaptor}
import org.mockito.{IdiomaticMockito, MockitoSugar}
import org.scalatest.{Matchers, WordSpec}
import user.org.mockito.captor.ArgCaptorTest._

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

class ArgCaptorTest extends WordSpec with MockitoSugar with Matchers {

  "Captor" should {

    "capture a simple AnyRef argument" in {
      val aMock  = mock[Foo]
      val captor = ArgCaptor[String]

      aMock.stringArgument("it worked!")

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

      captor.values should contain only ("it worked!", "it worked again!")
    }
  }

  "ValCaptor" should {
    "work with value case classes" in {
      val aMock  = mock[Foo]
      val captor = ValCaptor[Name]

      aMock.valueCaseClass(Name("Batman"))

      verify(aMock).valueCaseClass(captor)

      captor hasCaptured Name("Batman")
      captor.value shouldBe Name("Batman")
      captor.values should contain only Name("Batman")
    }

    "work with value non-case classes" in {
      val aMock  = mock[Foo]
      val captor = ValCaptor[Email]

      aMock.valueClass(new Email("batman@batcave.gotham"))

      verify(aMock).valueClass(captor)

      captor hasCaptured new Email("batman@batcave.gotham")
      captor.value shouldBe new Email("batman@batcave.gotham")
      captor.values should contain only new Email("batman@batcave.gotham")
    }

    "work with mixture of value class & value param" in new IdiomaticMockito {
      val aMock  = mock[Foo]
      val captor = ValCaptor[Email]

      aMock.valueClassAndValue(new Email("batman@batcave.gotham"), "42")

      aMock.valueClassAndValue(captor, "42") was called

      captor hasCaptured new Email("batman@batcave.gotham")
      captor.value shouldBe new Email("batman@batcave.gotham")
      captor.values should contain only new Email("batman@batcave.gotham")
    }
  }
}
