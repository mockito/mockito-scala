package org

import java.lang.reflect.Method

import org.mockito.ReflectionUtils.InvocationOnMockOps
import org.mockito.internal.ValueClassExtractor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.ScalaAnswer
import org.scalactic.Equality
import org.scalactic.TripleEquals._

import scala.reflect.ClassTag

package object mockito {

  /** Some forms of tagged types don't provide a ClassTag, given that sometimes we only use it to differentiate
   *  an InvocationOnMock from anything else, this is a safe default for those methods
   */
  private[mockito] def defaultClassTag[T]: ClassTag[T] = ClassTag.AnyRef.asInstanceOf[ClassTag[T]]

  def clazz[T](implicit classTag: ClassTag[T]): Class[T] = classTag.runtimeClass.asInstanceOf[Class[T]]

  implicit val InvocationOps: InvocationOnMock => InvocationOnMockOps = new InvocationOnMockOps(_)

  //noinspection ConvertExpressionToSAM
  def invocationToAnswer[T: ValueClassExtractor](f: InvocationOnMock => T): ScalaAnswer[T] =
    ScalaAnswer.lift(f.andThen(ValueClassExtractor[T].extractAs[T]))

  def functionToAnswer[T: ValueClassExtractor, P0](f: P0 => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0)))

  def functionToAnswer[T: ValueClassExtractor, P1, P0](f: (P0, P1) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1)))

  def functionToAnswer[T: ValueClassExtractor, P2, P1, P0](f: (P0, P1, P2) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2)))

  def functionToAnswer[T: ValueClassExtractor, P3, P2, P1, P0](f: (P0, P1, P2, P3) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3)))

  def functionToAnswer[T: ValueClassExtractor, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4)))

  def functionToAnswer[T: ValueClassExtractor, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5)))

  def functionToAnswer[T: ValueClassExtractor, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6)))

  def functionToAnswer[T: ValueClassExtractor, P7, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6), i.arg[P7](7)))

  def functionToAnswer[T: ValueClassExtractor, P8, P7, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6), i.arg[P7](7), i.arg[P8](8)))

  def functionToAnswer[T: ValueClassExtractor, P9, P8, P7, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6), i.arg[P7](7), i.arg[P8](8), i.arg[P9](9)))

  def functionToAnswer[T: ValueClassExtractor, P10, P9, P8, P7, P6, P5, P4, P3, P2, P1, P0](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): ScalaAnswer[T] =
    invocationToAnswer(
      i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6), i.arg[P7](7), i.arg[P8](8), i.arg[P9](9), i.arg[P10](10))
    )

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
