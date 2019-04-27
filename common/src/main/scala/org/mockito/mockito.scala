package org

import java.lang.reflect.Method

import org.mockito.internal.ValueClassExtractor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalactic.Equality
import org.scalactic.TripleEquals._

import scala.reflect.ClassTag

package object mockito {
  def clazz[T](implicit classTag: ClassTag[T]): Class[T] = classTag.runtimeClass.asInstanceOf[Class[T]]

  //noinspection ConvertExpressionToSAM
  def invocationToAnswer[T](f: InvocationOnMock => T)(implicit $vce: ValueClassExtractor[T]): Answer[Any] =
    new Answer[Any] with Serializable {
      override def answer(invocation: InvocationOnMock): Any = $vce.extract(f(invocation))
    }

  def functionToAnswer[T: ValueClassExtractor, P0](f: P0 => T): Answer[Any] =
    invocationToAnswer(i => f(i.getArgument[P0](0)))

  def functionToAnswer[T: ValueClassExtractor, P1, P0](f: (P0, P1) => T): Answer[Any] =
    invocationToAnswer(i => f(i.getArgument[P0](0), i.getArgument[P1](1)))

  def functionToAnswer[T: ValueClassExtractor, P2, P1, P0](f: (P0, P1, P2) => T): Answer[Any] =
    invocationToAnswer(i => f(i.getArgument[P0](0), i.getArgument[P1](1), i.getArgument[P2](2)))

  def functionToAnswer[T: ValueClassExtractor, P3, P2, P1, P0](f: (P0, P1, P2, P3) => T): Answer[Any] =
    invocationToAnswer(i => f(i.getArgument[P0](0), i.getArgument[P1](1), i.getArgument[P2](2), i.getArgument[P3](3)))

  def functionToAnswer[T: ValueClassExtractor, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4) => T): Answer[Any] =
    invocationToAnswer(i => f(i.getArgument[P0](0), i.getArgument[P1](1), i.getArgument[P2](2), i.getArgument[P3](3), i.getArgument[P4](4)))

  def functionToAnswer[T: ValueClassExtractor, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5) => T): Answer[Any] =
    invocationToAnswer(i =>
      f(i.getArgument[P0](0), i.getArgument[P1](1), i.getArgument[P2](2), i.getArgument[P3](3), i.getArgument[P4](4), i.getArgument[P5](5)))

  def functionToAnswer[T: ValueClassExtractor, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6) => T): Answer[Any] =
    invocationToAnswer(
      i =>
        f(i.getArgument[P0](0),
          i.getArgument[P1](1),
          i.getArgument[P2](2),
          i.getArgument[P3](3),
          i.getArgument[P4](4),
          i.getArgument[P5](5),
          i.getArgument[P6](6)))

  def functionToAnswer[T: ValueClassExtractor, P7, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): Answer[Any] =
    invocationToAnswer(
      i =>
        f(
          i.getArgument[P0](0),
          i.getArgument[P1](1),
          i.getArgument[P2](2),
          i.getArgument[P3](3),
          i.getArgument[P4](4),
          i.getArgument[P5](5),
          i.getArgument[P6](6),
          i.getArgument[P7](7)
      ))

  def functionToAnswer[T: ValueClassExtractor, P8, P7, P6, P5, P4, P3, P2, P1, P0](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): Answer[Any] =
    invocationToAnswer(
      i =>
        f(
          i.getArgument[P0](0),
          i.getArgument[P1](1),
          i.getArgument[P2](2),
          i.getArgument[P3](3),
          i.getArgument[P4](4),
          i.getArgument[P5](5),
          i.getArgument[P6](6),
          i.getArgument[P7](7),
          i.getArgument[P8](8)
      ))

  def functionToAnswer[T: ValueClassExtractor, P9, P8, P7, P6, P5, P4, P3, P2, P1, P0](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): Answer[Any] =
    invocationToAnswer(
      i =>
        f(
          i.getArgument[P0](0),
          i.getArgument[P1](1),
          i.getArgument[P2](2),
          i.getArgument[P3](3),
          i.getArgument[P4](4),
          i.getArgument[P5](5),
          i.getArgument[P6](6),
          i.getArgument[P7](7),
          i.getArgument[P8](8),
          i.getArgument[P9](9)
      ))

  def functionToAnswer[T: ValueClassExtractor, P10, P9, P8, P7, P6, P5, P4, P3, P2, P1, P0](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): Answer[Any] =
    invocationToAnswer(
      i =>
        f(
          i.getArgument[P0](0),
          i.getArgument[P1](1),
          i.getArgument[P2](2),
          i.getArgument[P3](3),
          i.getArgument[P4](4),
          i.getArgument[P5](5),
          i.getArgument[P6](6),
          i.getArgument[P7](7),
          i.getArgument[P8](8),
          i.getArgument[P9](9),
          i.getArgument[P10](10)
      ))

  //Look at org.mockito.internal.invocation.InvocationMatcher#hasSameMethod
  implicit val JavaMethodEquality: Equality[Method] = new Equality[Method] {
    override def areEqual(m1: Method, b: Any): Boolean = b match {
      case m2: Method =>
        m1.getName === m2.getName && m1.getParameterTypes === m2.getParameterTypes
      case _ => false
    }
  }

  def serialisableEquality[T]: Equality[T] = new Equality[T] with Serializable {
    override def areEqual(a: T, b: Any): Boolean = Equality.default[T].areEqual(a, b)
  }
}
