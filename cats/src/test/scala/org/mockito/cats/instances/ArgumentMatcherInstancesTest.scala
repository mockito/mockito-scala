package org.mockito.cats
package instances

import cats.{ Contravariant, Eq }
import cats.implicits._
import cats.laws.discipline._
import cats.laws.discipline.arbitrary._
import cats.laws.discipline.eq._
import org.mockito.{ ArgumentMatcher, ArgumentMatchers, ArgumentMatchersSugar, IdiomaticMockito }
import org.mockito.internal.matchers._
import org.scalacheck.Arbitrary
import org.scalatest.Matchers
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class ArgumentMatcherInstancesTest extends AnyFunSuiteLike with FunSuiteDiscipline with Configuration with ArgumentMatchersSugar with IdiomaticMockito with Matchers {
  implicit def eqArgumentMatcherExhaustive[A: ExhaustiveCheck]: Eq[ArgumentMatcher[A]] =
    Eq.instance((f, g) => ExhaustiveCheck[A].allValues.forall(a => f.matches(a) == g.matches(a)))

  implicit def arbArgumentMatcher[A](implicit a: Arbitrary[A => Boolean]): Arbitrary[ArgumentMatcher[A]] =
    Arbitrary(a.arbitrary.map(p => new ArgumentMatcher[A] { def matches(a: A) = p(a) }))

  checkAll("ArgumentMatcher[MiniInt]", ContravariantMonoidalTests[ArgumentMatcher].contravariantMonoidal[MiniInt, MiniInt, MiniInt])
  checkAll("ArgumentMatcher[MiniInt]", MonoidKTests[ArgumentMatcher].monoidK[MiniInt])

  test("contramapped ArgumentMatcher") {
    val aMock = mock[Foo]

    aMock.returnsOptionString(argThat(new StartsWith("prefix").contramap[String](_.toLowerCase))) returns Some("mocked!")

    aMock.returnsOptionString("PREFIX-foo") shouldBe Some("mocked!")
  }

  test("contramapped ArgumentMatcher via liftContravariant") {
    val aMock = mock[Foo]

    val renderDouble = Contravariant[ArgumentMatcher].liftContravariant[Double, String](n => f"$n%1.5f")
    aMock.takesDouble(argThat(renderDouble(new StartsWith("3")))) returns "mocked!"

    aMock.takesDouble(scala.math.Pi) shouldBe "mocked!"
  }

  test("tupled ArgumentMatchers") {
    val aMock = mock[Foo]

    aMock.takesTuple(argThat((new StartsWith("prefix"), new EqualsWithDelta(10, 2).narrow[Integer]).tupled)) returns "mocked!"

    aMock.takesTuple(("prefix-foo", 11)) shouldBe "mocked!"
  }

  test("tupled and contramapped ArgumentMatchers") {
    val aMock = mock[Foo]

    def split(s: String): (String, String) = s.split("/", 2) match { case Array(head, tail) => (head, tail) }
    aMock.returnsOptionString(
      argThat((new StartsWith("prefix1"), new StartsWith("prefix2")).contramapN[String](split))
    ) returns Some("mocked!")

    aMock.returnsOptionString("prefix1/prefix2/foo") shouldBe Some("mocked!")
  }

  test("combined ArgumentMatchers") {
    val aMock = mock[Foo]

    aMock.returnsOptionString(argThat(new StartsWith("prefix") <+> new EndsWith("suffix"))) returns Some("mocked!")

    aMock.returnsOptionString("prefix-foo-suffix") shouldBe Some("mocked!")
  }

  test("folded List of ArgumentMatchers") {
    val aMock = mock[Foo]

    val matcher = List(new StartsWith("prefix"), new Contains("middle"), new EndsWith("suffix")).foldK
    aMock.returnsOptionString(argThat(matcher)) returns Some("mocked!")

    aMock.returnsOptionString("prefix-middle-suffix") shouldBe Some("mocked!")
  }
}
