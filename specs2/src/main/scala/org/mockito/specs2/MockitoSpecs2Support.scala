package org.mockito.specs2

import org.specs2.matcher.MatchersImplicits._
import org.specs2.matcher.{ BeEqualTo, Expectations, MatchFailure, Matcher }

trait ArgThat {
  implicit def argThat[T](m: org.specs2.matcher.Matcher[T]): T = org.mockito.hamcrest.MockitoHamcrest.argThat(HamcrestMatcherAdapter(m))

  /** allows to use a hamcrest matchers to match parameters. */
  implicit def argThat[T, U <: T](m: org.hamcrest.Matcher[U]): T = org.mockito.hamcrest.MockitoHamcrest.argThat(m)
}

trait FunctionArgumentsLowImplicits extends ArgThat with Expectations {
  def partialCallMatching[A, R](a: A, m: Matcher[R]): PartialFunction[A, R] = {
    val partialMatcher: Matcher[PartialFunction[A, R]] = (f: PartialFunction[A, R]) =>
      try {
        (m ^^ ((pf: PartialFunction[A, R]) => pf(a))).apply(createExpectable(f))
      } catch {
        case _: MatchError => MatchFailure("ok", s"a PartialFunction defined for $a", createExpectable(f))
      }
    argThat(partialMatcher)
  }

  def partialFunctionCall[A, R](a: A, r: R): PartialFunction[A, R]                = partialCallMatching(a, new BeEqualTo(r))
  implicit def toPartialFunctionCall[A, R](values: (A, R)): PartialFunction[A, R] = partialFunctionCall(values._1, values._2)
  implicit def matcherToPartialFunctionCall[A, R](values: (A, Matcher[R])): PartialFunction[A, R] =
    partialCallMatching(values._1, values._2)
}

trait FunctionArguments extends FunctionArgumentsLowImplicits {
  def callMatching[A, R](a: A, m: Matcher[R]): A => R =
    argThat(m ^^ { (f: A => R) => f(a) })
  def functionCall[A, R](a: A, r: R): A => R                                = callMatching(a, new BeEqualTo(r))
  implicit def toFunctionCall[A, R](values: (A, R)): A => R                 = functionCall(values._1, values._2)
  implicit def matcherToFunctionCall[A, R](values: (A, Matcher[R])): A => R = callMatching(values._1, values._2)
  def callMatching2[T1, T2, R](t1: T1, t2: T2, m: Matcher[R]): Function2[T1, T2, R] =
    argThat(m ^^ { (f: Function2[T1, T2, R]) => f(t1, t2) })
  def functionCall2[T1, T2, R](t1: T1, t2: T2, r: R): Function2[T1, T2, R] = callMatching2(t1, t2, new BeEqualTo(r))
  implicit def toFunctionCall2[T1, T2, R](values: ((T1, T2), R)): Function2[T1, T2, R] =
    functionCall2(values._1._1, values._1._2, values._2)
  implicit def matcherToFunctionCall2[T1, T2, R](values: ((T1, T2), Matcher[R])): Function2[T1, T2, R] =
    callMatching2(values._1._1, values._1._2, values._2)

  def callMatching3[T1, T2, T3, R](t1: T1, t2: T2, t3: T3, m: Matcher[R]): Function3[T1, T2, T3, R] =
    argThat(m ^^ { (f: Function3[T1, T2, T3, R]) => f(t1, t2, t3) })
  def functionCall3[T1, T2, T3, R](t1: T1, t2: T2, t3: T3, r: R): Function3[T1, T2, T3, R] = callMatching3(t1, t2, t3, new BeEqualTo(r))
  implicit def toFunctionCall3[T1, T2, T3, R](values: ((T1, T2, T3), R)): Function3[T1, T2, T3, R] =
    functionCall3(values._1._1, values._1._2, values._1._3, values._2)
  implicit def matcherToFunctionCall3[T1, T2, T3, R](values: ((T1, T2, T3), Matcher[R])): Function3[T1, T2, T3, R] =
    callMatching3(values._1._1, values._1._2, values._1._3, values._2)

  def callMatching4[T1, T2, T3, T4, R](t1: T1, t2: T2, t3: T3, t4: T4, m: Matcher[R]): Function4[T1, T2, T3, T4, R] =
    argThat(m ^^ { (f: Function4[T1, T2, T3, T4, R]) => f(t1, t2, t3, t4) })
  def functionCall4[T1, T2, T3, T4, R](t1: T1, t2: T2, t3: T3, t4: T4, r: R): Function4[T1, T2, T3, T4, R] =
    callMatching4(t1, t2, t3, t4, new BeEqualTo(r))
  implicit def toFunctionCall4[T1, T2, T3, T4, R](values: ((T1, T2, T3, T4), R)): Function4[T1, T2, T3, T4, R] =
    functionCall4(values._1._1, values._1._2, values._1._3, values._1._4, values._2)
  implicit def matcherToFunctionCall4[T1, T2, T3, T4, R](values: ((T1, T2, T3, T4), Matcher[R])): Function4[T1, T2, T3, T4, R] =
    callMatching4(values._1._1, values._1._2, values._1._3, values._1._4, values._2)

  def callMatching5[T1, T2, T3, T4, T5, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, m: Matcher[R]): Function5[T1, T2, T3, T4, T5, R] =
    argThat(m ^^ { (f: Function5[T1, T2, T3, T4, T5, R]) => f(t1, t2, t3, t4, t5) })
  def functionCall5[T1, T2, T3, T4, T5, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, r: R): Function5[T1, T2, T3, T4, T5, R] =
    callMatching5(t1, t2, t3, t4, t5, new BeEqualTo(r))
  implicit def toFunctionCall5[T1, T2, T3, T4, T5, R](values: ((T1, T2, T3, T4, T5), R)): Function5[T1, T2, T3, T4, T5, R] =
    functionCall5(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._2)
  implicit def matcherToFunctionCall5[T1, T2, T3, T4, T5, R](values: ((T1, T2, T3, T4, T5), Matcher[R])): Function5[T1, T2, T3, T4, T5, R] =
    callMatching5(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._2)

  def callMatching6[T1, T2, T3, T4, T5, T6, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, m: Matcher[R]): Function6[T1, T2, T3, T4, T5, T6, R] =
    argThat(m ^^ { (f: Function6[T1, T2, T3, T4, T5, T6, R]) => f(t1, t2, t3, t4, t5, t6) })
  def functionCall6[T1, T2, T3, T4, T5, T6, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, r: R): Function6[T1, T2, T3, T4, T5, T6, R] =
    callMatching6(t1, t2, t3, t4, t5, t6, new BeEqualTo(r))
  implicit def toFunctionCall6[T1, T2, T3, T4, T5, T6, R](values: ((T1, T2, T3, T4, T5, T6), R)): Function6[T1, T2, T3, T4, T5, T6, R] =
    functionCall6(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._2)
  implicit def matcherToFunctionCall6[T1, T2, T3, T4, T5, T6, R](values: ((T1, T2, T3, T4, T5, T6), Matcher[R])): Function6[T1, T2, T3, T4, T5, T6, R] =
    callMatching6(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._2)

  def callMatching7[T1, T2, T3, T4, T5, T6, T7, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, m: Matcher[R]): Function7[T1, T2, T3, T4, T5, T6, T7, R] =
    argThat(m ^^ { (f: Function7[T1, T2, T3, T4, T5, T6, T7, R]) => f(t1, t2, t3, t4, t5, t6, t7) })
  def functionCall7[T1, T2, T3, T4, T5, T6, T7, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, r: R): Function7[T1, T2, T3, T4, T5, T6, T7, R] =
    callMatching7(t1, t2, t3, t4, t5, t6, t7, new BeEqualTo(r))
  implicit def toFunctionCall7[T1, T2, T3, T4, T5, T6, T7, R](values: ((T1, T2, T3, T4, T5, T6, T7), R)): Function7[T1, T2, T3, T4, T5, T6, T7, R] =
    functionCall7(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._1._7, values._2)
  implicit def matcherToFunctionCall7[T1, T2, T3, T4, T5, T6, T7, R](values: ((T1, T2, T3, T4, T5, T6, T7), Matcher[R])): Function7[T1, T2, T3, T4, T5, T6, T7, R] =
    callMatching7(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._1._7, values._2)

  def callMatching8[T1, T2, T3, T4, T5, T6, T7, T8, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, m: Matcher[R])
      : Function8[T1, T2, T3, T4, T5, T6, T7, T8, R] =
    argThat(m ^^ { (f: Function8[T1, T2, T3, T4, T5, T6, T7, T8, R]) => f(t1, t2, t3, t4, t5, t6, t7, t8) })
  def functionCall8[T1, T2, T3, T4, T5, T6, T7, T8, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, r: R): Function8[T1, T2, T3, T4, T5, T6, T7, T8, R] =
    callMatching8(t1, t2, t3, t4, t5, t6, t7, t8, new BeEqualTo(r))
  implicit def toFunctionCall8[T1, T2, T3, T4, T5, T6, T7, T8, R](values: ((T1, T2, T3, T4, T5, T6, T7, T8), R)): Function8[T1, T2, T3, T4, T5, T6, T7, T8, R] =
    functionCall8(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._1._7, values._1._8, values._2)
  implicit def matcherToFunctionCall8[T1, T2, T3, T4, T5, T6, T7, T8, R](values: ((T1, T2, T3, T4, T5, T6, T7, T8), Matcher[R])): Function8[T1, T2, T3, T4, T5, T6, T7, T8, R] =
    callMatching8(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._1._7, values._1._8, values._2)

  def callMatching9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, m: Matcher[R])
      : Function9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R] =
    argThat(m ^^ { (f: Function9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R]) => f(t1, t2, t3, t4, t5, t6, t7, t8, t9) })
  def functionCall9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, r: R)
      : Function9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R] = callMatching9(t1, t2, t3, t4, t5, t6, t7, t8, t9, new BeEqualTo(r))
  implicit def toFunctionCall9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9), R)): Function9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R] =
    functionCall9(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._1._7, values._1._8, values._1._9, values._2)
  implicit def matcherToFunctionCall9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9), Matcher[R])
  ): Function9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R] =
    callMatching9(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._1._7, values._1._8, values._1._9, values._2)

  def callMatching10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, m: Matcher[R])
      : Function10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R] =
    argThat(m ^^ { (f: Function10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R]) => f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10) })
  def functionCall10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, r: R)
      : Function10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R] =
    callMatching10(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, new BeEqualTo(r))
  implicit def toFunctionCall10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R)
  ): Function10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R] =
    functionCall10(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._1._7, values._1._8, values._1._9, values._1._10, values._2)
  implicit def matcherToFunctionCall10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), Matcher[R])
  ): Function10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R] =
    callMatching10(values._1._1, values._1._2, values._1._3, values._1._4, values._1._5, values._1._6, values._1._7, values._1._8, values._1._9, values._1._10, values._2)

  def callMatching11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, m: Matcher[R])
      : Function11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R] =
    argThat(m ^^ { (f: Function11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R]) => f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) })
  def functionCall11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, r: R)
      : Function11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R] =
    callMatching11(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, new BeEqualTo(r))
  implicit def toFunctionCall11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R)
  ): Function11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R] =
    functionCall11(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._2
    )
  implicit def matcherToFunctionCall11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), Matcher[R])
  ): Function11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R] =
    callMatching11(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._2
    )

  def callMatching12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      m: Matcher[R]
  ): Function12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R] =
    argThat(m ^^ { (f: Function12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R]) => f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12) })
  def functionCall12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      r: R
  ): Function12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R] =
    callMatching12(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, new BeEqualTo(r))
  implicit def toFunctionCall12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R)
  ): Function12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R] =
    functionCall12(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._2
    )
  implicit def matcherToFunctionCall12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), Matcher[R])
  ): Function12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R] =
    callMatching12(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._2
    )

  def callMatching13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      m: Matcher[R]
  ): Function13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R] =
    argThat(m ^^ { (f: Function13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R]) => f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13) })
  def functionCall13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      r: R
  ): Function13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R] =
    callMatching13(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, new BeEqualTo(r))
  implicit def toFunctionCall13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R)
  ): Function13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R] =
    functionCall13(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._2
    )
  implicit def matcherToFunctionCall13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), Matcher[R])
  ): Function13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R] =
    callMatching13(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._2
    )

  def callMatching14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      m: Matcher[R]
  ): Function14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R] =
    argThat(m ^^ { (f: Function14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R]) => f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14) })
  def functionCall14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      r: R
  ): Function14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R] =
    callMatching14(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, new BeEqualTo(r))
  implicit def toFunctionCall14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R)
  ): Function14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R] =
    functionCall14(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._2
    )
  implicit def matcherToFunctionCall14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), Matcher[R])
  ): Function14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R] =
    callMatching14(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._2
    )

  def callMatching15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      m: Matcher[R]
  ): Function15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R] =
    argThat(m ^^ { (f: Function15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R]) => f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15) })
  def functionCall15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      r: R
  ): Function15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R] =
    callMatching15(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, new BeEqualTo(r))
  implicit def toFunctionCall15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R)
  ): Function15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R] =
    functionCall15(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._2
    )
  implicit def matcherToFunctionCall15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), Matcher[R])
  ): Function15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R] =
    callMatching15(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._2
    )

  def callMatching16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      m: Matcher[R]
  ): Function16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R] =
    argThat(m ^^ { (f: Function16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R]) =>
      f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16)
    })
  def functionCall16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      r: R
  ): Function16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R] =
    callMatching16(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, new BeEqualTo(r))
  implicit def toFunctionCall16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R)
  ): Function16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R] =
    functionCall16(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._2
    )
  implicit def matcherToFunctionCall16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), Matcher[R])
  ): Function16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R] =
    callMatching16(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._2
    )

  def callMatching17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      m: Matcher[R]
  ): Function17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R] =
    argThat(m ^^ { (f: Function17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R]) =>
      f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)
    })
  def functionCall17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      r: R
  ): Function17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R] =
    callMatching17(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, new BeEqualTo(r))
  implicit def toFunctionCall17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R)
  ): Function17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R] =
    functionCall17(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._2
    )
  implicit def matcherToFunctionCall17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), Matcher[R])
  ): Function17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R] =
    callMatching17(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._2
    )

  def callMatching18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      m: Matcher[R]
  ): Function18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R] =
    argThat(m ^^ { (f: Function18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R]) =>
      f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18)
    })
  def functionCall18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      r: R
  ): Function18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R] =
    callMatching18(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, new BeEqualTo(r))
  implicit def toFunctionCall18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R)
  ): Function18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R] =
    functionCall18(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._2
    )
  implicit def matcherToFunctionCall18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), Matcher[R])
  ): Function18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R] =
    callMatching18(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._2
    )

  def callMatching19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      t19: T19,
      m: Matcher[R]
  ): Function19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R] =
    argThat(m ^^ { (f: Function19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R]) =>
      f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19)
    })
  def functionCall19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      t19: T19,
      r: R
  ): Function19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R] =
    callMatching19(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, new BeEqualTo(r))
  implicit def toFunctionCall19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R)
  ): Function19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R] =
    functionCall19(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._1._19,
      values._2
    )
  implicit def matcherToFunctionCall19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), Matcher[R])
  ): Function19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R] =
    callMatching19(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._1._19,
      values._2
    )

  def callMatching20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      t19: T19,
      t20: T20,
      m: Matcher[R]
  ): Function20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R] =
    argThat(m ^^ { (f: Function20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R]) =>
      f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20)
    })
  def functionCall20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      t19: T19,
      t20: T20,
      r: R
  ): Function20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R] =
    callMatching20(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, new BeEqualTo(r))
  implicit def toFunctionCall20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R)
  ): Function20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R] =
    functionCall20(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._1._19,
      values._1._20,
      values._2
    )
  implicit def matcherToFunctionCall20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), Matcher[R])
  ): Function20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R] =
    callMatching20(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._1._19,
      values._1._20,
      values._2
    )

  def callMatching21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      t19: T19,
      t20: T20,
      t21: T21,
      m: Matcher[R]
  ): Function21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R] =
    argThat(m ^^ { (f: Function21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R]) =>
      f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21)
    })
  def functionCall21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      t19: T19,
      t20: T20,
      t21: T21,
      r: R
  ): Function21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R] =
    callMatching21(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, new BeEqualTo(r))
  implicit def toFunctionCall21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R)
  ): Function21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R] =
    functionCall21(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._1._19,
      values._1._20,
      values._1._21,
      values._2
    )
  implicit def matcherToFunctionCall21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), Matcher[R])
  ): Function21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R] =
    callMatching21(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._1._19,
      values._1._20,
      values._1._21,
      values._2
    )

  def callMatching22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      t19: T19,
      t20: T20,
      t21: T21,
      t22: T22,
      m: Matcher[R]
  ): Function22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R] =
    argThat(m ^^ { (f: Function22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R]) =>
      f(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22)
    })
  def functionCall22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](
      t1: T1,
      t2: T2,
      t3: T3,
      t4: T4,
      t5: T5,
      t6: T6,
      t7: T7,
      t8: T8,
      t9: T9,
      t10: T10,
      t11: T11,
      t12: T12,
      t13: T13,
      t14: T14,
      t15: T15,
      t16: T16,
      t17: T17,
      t18: T18,
      t19: T19,
      t20: T20,
      t21: T21,
      t22: T22,
      r: R
  ): Function22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R] =
    callMatching22(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, new BeEqualTo(r))
  implicit def toFunctionCall22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R)
  ): Function22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R] =
    functionCall22(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._1._19,
      values._1._20,
      values._1._21,
      values._1._22,
      values._2
    )
  implicit def matcherToFunctionCall22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](
      values: ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), Matcher[R])
  ): Function22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R] =
    callMatching22(
      values._1._1,
      values._1._2,
      values._1._3,
      values._1._4,
      values._1._5,
      values._1._6,
      values._1._7,
      values._1._8,
      values._1._9,
      values._1._10,
      values._1._11,
      values._1._12,
      values._1._13,
      values._1._14,
      values._1._15,
      values._1._16,
      values._1._17,
      values._1._18,
      values._1._19,
      values._1._20,
      values._1._21,
      values._1._22,
      values._2
    )
}

trait MockitoSpecs2Support extends FunctionArguments
