package user.org.mockito

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito, MockitoScalaSession }
import IdiomaticMockito._
import ArgumentMatchersSugar._



class Issue256 extends AnyWordSpec with Matchers{

  trait Foo {
    def test[A](a: A): A
  }

  "mockito" should {
    "allow stubbing the same method multiple times" in {
      MockitoScalaSession().run {
        val foo = mock[Foo]
        foo.test[String](argThat((s: String) => s.startsWith("foo"))) returns "foo"
        foo.test[Int](argThat((n: Int) => n > 10)) returns 42

        foo.test("fooSSS") shouldBe "foo"
        foo.test(11) shouldBe 42
      }
    }
  }
}
