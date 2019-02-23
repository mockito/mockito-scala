package org.mockito.matchers

import org.mockito.internal.ValueClassExtractor
import org.mockito.{ArgumentMatcher, ArgumentMatchers => JavaMatchers}
import org.scalactic.Equality
import org.scalactic.TripleEquals._

import scala.collection.mutable

trait EqMatchers_212 {

  /**
   * Creates a matcher that delegates on {{org.scalactic.Equality}} so you can always customise how the values are compared
   * Also works with value classes
   */
  def eqTo[T](value: T, others: T*)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T]): T = {
    lazy val rawValues: Seq[T] = Seq(value) ++ others
    JavaMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = v match {
        case a: mutable.WrappedArray[_] if rawValues.length == a.length =>
          (rawValues zip a) forall {
            case (expected, got) => expected.asInstanceOf[T] === got
          }
        case other =>
          $vce.extract(value).asInstanceOf[T] === other
      }
      override def toString: String = s"eqTo(${rawValues.mkString(", ")})"
    })
    value
  }

  /**
   * It was intended to be used instead of eqTo when the argument is a value class,
   * but eqTo now supports value classes so it is not needed anymore
   */
  @deprecated("Use 'eqTo' instead", since = "1.0.2")
  def eqToVal[T](value: T)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T]): T = eqTo(value)
}
