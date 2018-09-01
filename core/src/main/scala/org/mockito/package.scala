package org

import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import scala.reflect.ClassTag

package object mockito {
  def clazz[T <: AnyRef](implicit classTag: ClassTag[T]): Class[T] = classTag.runtimeClass.asInstanceOf[Class[T]]

  //noinspection ConvertExpressionToSAM
  def invocationToAnswer[T](f: InvocationOnMock => T): Answer[T] = new Answer[T] {
    override def answer(invocation: InvocationOnMock): T = f(invocation)
  }

  def functionToAnswer[T, P0](f: P0 => T): Answer[T] = invocationToAnswer(i => f(i.getArgument[P0](0)))

  def functionToAnswer[T, P1, P0](f: (P0, P1) => T): Answer[T] =
    invocationToAnswer(i => f(i.getArgument[P0](0), i.getArgument[P1](1)))

  def functionToAnswer[T, P2, P1, P0](f: (P0, P1, P2) => T): Answer[T] =
    invocationToAnswer(i => f(i.getArgument[P0](0), i.getArgument[P1](1), i.getArgument[P2](2)))

  def functionToAnswer[T, P3, P2, P1, P0](f: (P0, P1, P2, P3) => T): Answer[T] =
    invocationToAnswer(i => f(i.getArgument[P0](0), i.getArgument[P1](1), i.getArgument[P2](2), i.getArgument[P3](3)))

  def functionToAnswer[T, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4) => T): Answer[T] =
    invocationToAnswer(i =>
      f(i.getArgument[P0](0), i.getArgument[P1](1), i.getArgument[P2](2), i.getArgument[P3](3), i.getArgument[P4](4)))

  def functionToAnswer[T, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5) => T): Answer[T] =
    invocationToAnswer(
      i =>
        f(i.getArgument[P0](0),
          i.getArgument[P1](1),
          i.getArgument[P2](2),
          i.getArgument[P3](3),
          i.getArgument[P4](4),
          i.getArgument[P5](5)))

  def functionToAnswer[T, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6) => T): Answer[T] =
    invocationToAnswer(
      i =>
        f(i.getArgument[P0](0),
          i.getArgument[P1](1),
          i.getArgument[P2](2),
          i.getArgument[P3](3),
          i.getArgument[P4](4),
          i.getArgument[P5](5),
          i.getArgument[P6](6)))

  def functionToAnswer[T, P7, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): Answer[T] =
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

  def functionToAnswer[T, P8, P7, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): Answer[T] =
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

  def functionToAnswer[T, P9, P8, P7, P6, P5, P4, P3, P2, P1, P0](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): Answer[T] =
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

  def functionToAnswer[T, P10, P9, P8, P7, P6, P5, P4, P3, P2, P1, P0](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): Answer[T] =
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
}
