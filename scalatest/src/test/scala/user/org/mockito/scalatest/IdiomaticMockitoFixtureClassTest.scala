package user.org.mockito.scalatest

import org.mockito.exceptions.verification.{ NeverWantedButInvoked, NoInteractionsWanted }
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.{ fixture, Outcome }

class IdiomaticMockitoFixtureClassTest extends fixture.FlatSpec with IdiomaticMockito with Matchers {
  class Foo {
    def bar(a: String) = "bar"
    def baz            = "baz"
  }

  class FixtureParam {
    val foo: Foo = mock[Foo]
  }

  def withFixture(test: OneArgTest): Outcome = {
    val theFixture = new FixtureParam
    withFixture(test.toNoArgTest(theFixture))
  }

  "Mockito" should "verifyNoMoreInteractions fixture objects" in { f: FixtureParam =>
    "mocked" willBe returned by f.foo.bar("pepe")

    f.foo wasNever called

    f.foo.bar("pepe") shouldBe "mocked"

    a[NoInteractionsWanted] should be thrownBy {
      f.foo wasNever called
    }
  }

  "Mockito" should "verify no calls on fixture objects methods" in { f: FixtureParam =>
    "mocked" willBe returned by f.foo.bar("pepe")
    "mocked" willBe returned by f.foo.baz

    f.foo.bar("pepe") shouldBe "mocked"
    f.foo.baz shouldBe "mocked"

    a[NeverWantedButInvoked] should be thrownBy {
      f.foo.bar(*) wasNever called
    }
    a[NeverWantedButInvoked] should be thrownBy {
      f.foo.baz wasNever called
    }
  }
}
