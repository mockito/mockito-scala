package user.org.mockito.scalatest

import org.mockito.PrefixExpectations
import org.mockito.exceptions.misusing.NotAMockException
import org.mockito.exceptions.verification.{ NeverWantedButInvoked, NoInteractionsWanted }
import org.mockito.scalatest.IdiomaticMockitoBase
import org.scalatest.matchers.should.Matchers
import org.scalatest.{ fixture, Outcome }

class IdiomaticMockitoWithExpectFixtureClassTest extends fixture.FlatSpec with IdiomaticMockitoBase with PrefixExpectations with Matchers {
  class Foo {
    def bar(a: String) = "bar"
    def baz            = "baz"
  }

  class FixtureParam {
    val foo: Foo = mock[Foo]
  }

  def withFixture(test: OneArgTest): Outcome = {
    val theFixture = new FixtureParam
    super.withFixture(test.toNoArgTest(theFixture))
  }

  "expect no calls TO" should "verify no calls on fixture objects methods" in { f: FixtureParam =>
    "mocked" willBe returned by f.foo.bar("pepe")
    "mocked" willBe returned by f.foo.baz

    f.foo.bar("pepe") shouldBe "mocked"
    f.foo.baz shouldBe "mocked"

    a[NeverWantedButInvoked] should be thrownBy {
      expect no calls to f.foo.bar(*)
    }
    a[NeverWantedButInvoked] should be thrownBy {
      expect no calls to f.foo.baz
    }
  }

  "expect no calls ON" should "verify no calls on a mock inside a fixture object" in { f: FixtureParam =>
    f.foo.bar("pepe") returns "mocked"

    expect no calls on f.foo

    f.foo.bar("pepe") shouldBe "mocked"

    a[NoInteractionsWanted] should be thrownBy {
      expect no calls on f.foo
    }
  }

  it should "prevent usage of 'no calls to' when 'no calls on' is intended" in { f: FixtureParam =>
    the[NotAMockException] thrownBy {
      expect no calls to f.foo
    } should have message
    """'expect no calls to <?>' requires an argument which is 'a method call on a mock',
        |  but looks like [f.foo] is not a method call on a mock. Is it a mock object?
        |
        |The following would be correct (note the usage of 'calls to' vs 'calls on'):
        |    expect no calls to aMock.bar(*)
        |    expect no calls on aMock
        |""".stripMargin
  }

  "expect noMore calls on" should "verify no more calls on a mock inside a fixture object" in { f: FixtureParam =>
    f.foo.bar("pepe") returns "mocked"

    f.foo.bar("pepe") shouldBe "mocked"

    a[NoInteractionsWanted] should be thrownBy {
      expect noMore calls on f.foo
    }

    expect a call to f.foo.bar(*)
    expect noMore calls on f.foo
  }
}
