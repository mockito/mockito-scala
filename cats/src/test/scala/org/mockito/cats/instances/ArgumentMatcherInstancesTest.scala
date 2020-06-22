package org.mockito.cats
package instances

import cats.Eq
import cats.implicits._
import cats.laws.discipline._
import cats.laws.discipline.arbitrary._
import cats.laws.discipline.eq._
import org.mockito.ArgumentMatcher
import org.scalacheck.Arbitrary
import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class ArgumentMatcherInstancesTest extends AnyFunSuiteLike with FunSuiteDiscipline with Configuration {
  implicit def eqArgumentMatcherExhaustive[A: ExhaustiveCheck]: Eq[ArgumentMatcher[A]] =
    Eq.instance((f, g) => ExhaustiveCheck[A].allValues.forall(a => f.matches(a) == g.matches(a)))

  implicit def arbArgumentMatcher[A](implicit a: Arbitrary[A => Boolean]): Arbitrary[ArgumentMatcher[A]] =
    Arbitrary(a.arbitrary.map(p => new ArgumentMatcher[A] { def matches(a: A) = p(a) }))

  checkAll("ArgumentMatcher[MiniInt]", ContravariantMonoidalTests[ArgumentMatcher].contravariantMonoidal[MiniInt, MiniInt, MiniInt])
  checkAll("ArgumentMatcher[MiniInt]", MonoidKTests[ArgumentMatcher].monoidK[MiniInt])
}
