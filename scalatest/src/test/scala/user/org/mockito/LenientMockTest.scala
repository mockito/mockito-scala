package user.org.mockito

import org.mockito.*
import org.mockito.exceptions.misusing.UnnecessaryStubbingException
import org.mockito.quality.Strictness
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class LenientMockTest extends AnyWordSpec with Matchers {

  "lenientMock" should {
    "create a mock with LENIENT strictness using MockitoSugar" in new MockitoSugar {
      val aMock: Foo = lenientMock[Foo]
      Mockito.mockingDetails(aMock).getMockCreationSettings.getStrictness shouldBe Strictness.LENIENT
    }

    "create a mock with LENIENT strictness directly from MockitoSugar object" in {
      val aMock = MockitoSugar.lenientMock[Foo]
      Mockito.mockingDetails(aMock).getMockCreationSettings.getStrictness shouldBe Strictness.LENIENT
    }

    "create a mock with LENIENT strictness using IdiomaticMockito" in new IdiomaticMockito {
      val aMock: Foo = lenientMock[Foo]
      Mockito.mockingDetails(aMock).getMockCreationSettings.getStrictness shouldBe Strictness.LENIENT
    }

    "allow stubbing and invocation like a standard mock" in new MockitoSugar {
      val aMock: Foo = lenientMock[Foo]

      when(aMock.bar) thenReturn "mocked"
      aMock.bar shouldBe "mocked"
    }

    "allow idiomatic stubbing and verification" in new IdiomaticMockito with ArgumentMatchersSugar {
      val aMock: Foo = lenientMock[Foo]

      aMock.bar returns "mocked"
      aMock.bar shouldBe "mocked"
      aMock.bar was called
    }

    "not fail MockitoScalaSession on unused stubs" in {
      // Normal mock throws UnnecessaryStubbingException on unused stubs in a session
      a[UnnecessaryStubbingException] should be thrownBy
      MockitoScalaSession().run {
        val strictMock = MockitoSugar.mock[Foo]
        MockitoSugar.when(strictMock.bar) thenReturn "mocked"
        // stub is unused, session end should fail
      }

      // lenientMock does NOT fail the session on unused stubs
      MockitoScalaSession().run {
        val aMock = MockitoSugar.lenientMock[Foo]
        MockitoSugar.when(aMock.bar) thenReturn "mocked"
        // stub is unused, but because it's lenient, no exception is thrown
      }
    }

    "support by-name parameters" in new IdiomaticMockito with ArgumentMatchersSugar {
      val aMock: Baz = lenientMock[Baz]

      aMock.byNameMethod(*) returns 42
      aMock.byNameMethod(1 + 2) shouldBe 42
    }

    "support varargs parameters" in new IdiomaticMockito with ArgumentMatchersSugar {
      val aMock: Baz = lenientMock[Baz]

      aMock.varargMethod("prefix", 1, 2, 3) returns 99
      aMock.varargMethod("prefix", 1, 2, 3) shouldBe 99
    }

    "support default arguments" in new IdiomaticMockito with ArgumentMatchersSugar {
      val aMock: Foo = lenientMock[Foo]

      aMock.iHaveSomeDefaultArguments("custom", "default value") returns "mocked"
      aMock.iHaveSomeDefaultArguments("custom") shouldBe "mocked"
    }
  }
}
