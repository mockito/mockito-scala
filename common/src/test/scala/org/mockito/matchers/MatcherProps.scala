package org.mockito
package matchers

import cats.laws.discipline.MiniInt
import cats.laws.discipline.arbitrary._
import org.mockito.internal.matchers._
import org.scalacheck._

import Arbitrary.arbitrary
import Gen._
import Prop._

class MatcherProps extends Properties("matchers") {
  import Generators._

  property("AllOf") = forAll(chooseNum(0, 8))(length =>
    forAll(listOfN(length, arbitrary[ArgumentMatcher[MiniInt]]), arbitrary[MiniInt]) { case (matchers, value) =>
      val allOf     = AllOf(matchers: _*)
      val stringRep = allOf.toString

      classify(allOf.matches(value), "matches", "doesn't match") {
        (allOf.matches(value) ?= matchers.forall(_.matches(value))) :| "matches all underlying" &&
        matchers.iff {
          case Nil            => stringRep ?= "<any>"
          case matcher :: Nil => stringRep ?= matcher.toString()
          case _              => stringRep ?= s"allOf(${matchers.mkString(", ")})"
        } :| "renders to string correctly"

      }
    }
  )

  property("ProductOf") = forAll { (ma: ArgumentMatcher[MiniInt], mb: ArgumentMatcher[String], a: MiniInt, b: String) =>
    val productOf = ProductOf(ma, mb)
    val product   = (a, b)

    val maMatches      = ma.matches(a)
    val mbMatches      = mb.matches(b)
    val productMatches = productOf.matches(product)

    classify(productMatches, "matches", "doesn't match") {
      all(
        (productMatches ==> maMatches) :| "ma matches if product does",
        (productMatches ==> mbMatches) :| "mb matches if product does",
        ((maMatches && mbMatches) ==> productMatches) :| "product matches if both ma and mb do",
        (productOf.toString ?= s"productOf($ma, $mb)") :| "renders to string correctly"
      )
    }
  }

  property("Transformed") = forAll { (ma: ArgumentMatcher[String], f: MiniInt => String, value: MiniInt) =>
    val transformed = Transformed(ma)(f)
    val matches     = transformed.matches(value)
    classify(matches, "matches", "doesn't match") {
      (matches ?= ma.matches(f(value))) :| "matches if underlying matches transfomed value" &&
      (transformed.toString ?= s"transformed($ma: $f)") :| "renders to string correctly"
    }
  }
}
