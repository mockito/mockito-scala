package org.mockito
package matchers

import org.scalacheck.Arbitrary

object Generators {
  implicit def arbArgumentMatcher[A](implicit a: Arbitrary[A => Boolean]): Arbitrary[ArgumentMatcher[A]] =
    Arbitrary(a.arbitrary.map(p => new ArgumentMatcher[A] { def matches(a: A) = p(a) }))
}
