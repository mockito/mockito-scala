package org.mockito.captor

import org.mockito.MockitoSugar
import org.mockito.captor.ArgCaptorTest._
import org.scalatest.{ Matchers, WordSpec }

object ArgCaptorTest {
  case class Name(name: String) extends AnyVal

  class Email(val email: String) extends AnyVal

  class Foo {
    def stringArgument(s: String): String = s

    def intArgument(i: Int): Int = i

    def complexArgument(m: Map[String, Int]): Map[String, Int] = m

    def valueCaseClass(name: Name): String = name.name

    def valueClass(email: Email): String = email.email
  }
}

class ArgCaptorTest extends WordSpec with MockitoSugar with Matchers {

  "Captor" should {

    "capture a simple AnyRef argument" in {
      val aMock  = mock[Foo]
      val captor = Captor[String]

      aMock.stringArgument("it worked!")

      verify(aMock).stringArgument(captor)

      captor <-> "it worked!"
    }

    "capture a simple AnyVal argument" in {
      val aMock  = mock[Foo]
      val captor = Captor[Int]

      aMock.intArgument(42)

      verify(aMock).intArgument(captor)

      captor <-> 42
    }

    "capture a complex argument" in {
      val aMock  = mock[Foo]
      val captor = Captor[Map[String, Int]]

      aMock.complexArgument(Map("Works" -> 1))

      verify(aMock).complexArgument(captor)

      captor <-> Map("Works" -> 1)
    }

    "expose the captured value to use with custom matchers" in {
      val aMock  = mock[Foo]
      val captor = Captor[String]

      aMock.stringArgument("it worked!")

      verify(aMock).stringArgument(captor)

      captor.value shouldBe "it worked!"
    }

    "expose all the captured values to use with custom matchers" in {
      val aMock  = mock[Foo]
      val captor = Captor[String]

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

      captor <-> Name("Batman")
      captor.value shouldBe Name("Batman")
      captor.values should contain only Name("Batman")
    }

    "work with value non-case classes" in {
      val aMock  = mock[Foo]
      val captor = ValCaptor[Email]

      aMock.valueClass(new Email("batman@batcave.gotham"))

      verify(aMock).valueClass(captor)

      captor <-> new Email("batman@batcave.gotham")
      captor.value shouldBe new Email("batman@batcave.gotham")
      captor.values should contain only new Email("batman@batcave.gotham")
    }
  }
}
