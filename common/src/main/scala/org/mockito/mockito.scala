package org

import java.lang.reflect.Method

import org.mockito.ReflectionUtils.InvocationOnMockOps
import org.mockito.internal.{ ValueClassExtractor, ValueClassWrapper }
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

  def invocationToAnswer[T: ValueClassExtractor](f: InvocationOnMock => T): ScalaAnswer[T] =
    ScalaAnswer.lift(f.andThen(ValueClassExtractor[T].extractAs[T]))

  def functionToAnswer[T: ValueClassExtractor, P0: ValueClassWrapper](f: P0 => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0)))

  def functionToAnswer[T: ValueClassExtractor, P0: ValueClassWrapper, P1: ValueClassWrapper](f: (P0, P1) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1)))

  def functionToAnswer[T: ValueClassExtractor, P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper](f: (P0, P1, P2) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2)))

  def functionToAnswer[T: ValueClassExtractor, P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper](
      f: (P0, P1, P2, P3) => T
  ): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3)))

  def functionToAnswer[T: ValueClassExtractor, P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper](
      f: (P0, P1, P2, P3, P4) => T
  ): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4)))

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5)))

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6)))

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6), i.arg[P7](7)))

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6), i.arg[P7](7), i.arg[P8](8)))

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): ScalaAnswer[T] =
    invocationToAnswer(i => f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6), i.arg[P7](7), i.arg[P8](8), i.arg[P9](9)))

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(i.arg[P0](0), i.arg[P1](1), i.arg[P2](2), i.arg[P3](3), i.arg[P4](4), i.arg[P5](5), i.arg[P6](6), i.arg[P7](7), i.arg[P8](8), i.arg[P9](9), i.arg[P10](10))
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13),
        i.arg[P14](14)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13),
        i.arg[P14](14),
        i.arg[P15](15)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13),
        i.arg[P14](14),
        i.arg[P15](15),
        i.arg[P16](16)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13),
        i.arg[P14](14),
        i.arg[P15](15),
        i.arg[P16](16),
        i.arg[P17](17)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13),
        i.arg[P14](14),
        i.arg[P15](15),
        i.arg[P16](16),
        i.arg[P17](17),
        i.arg[P18](18)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13),
        i.arg[P14](14),
        i.arg[P15](15),
        i.arg[P16](16),
        i.arg[P17](17),
        i.arg[P18](18),
        i.arg[P19](19)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper,
      P20: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13),
        i.arg[P14](14),
        i.arg[P15](15),
        i.arg[P16](16),
        i.arg[P17](17),
        i.arg[P18](18),
        i.arg[P19](19),
        i.arg[P20](20)
      )
    )

  def functionToAnswer[
      T: ValueClassExtractor,
      P0: ValueClassWrapper,
      P1: ValueClassWrapper,
      P2: ValueClassWrapper,
      P3: ValueClassWrapper,
      P4: ValueClassWrapper,
      P5: ValueClassWrapper,
      P6: ValueClassWrapper,
      P7: ValueClassWrapper,
      P8: ValueClassWrapper,
      P9: ValueClassWrapper,
      P10: ValueClassWrapper,
      P11: ValueClassWrapper,
      P12: ValueClassWrapper,
      P13: ValueClassWrapper,
      P14: ValueClassWrapper,
      P15: ValueClassWrapper,
      P16: ValueClassWrapper,
      P17: ValueClassWrapper,
      P18: ValueClassWrapper,
      P19: ValueClassWrapper,
      P20: ValueClassWrapper,
      P21: ValueClassWrapper
  ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) => T): ScalaAnswer[T] =
    invocationToAnswer(i =>
      f(
        i.arg[P0](0),
        i.arg[P1](1),
        i.arg[P2](2),
        i.arg[P3](3),
        i.arg[P4](4),
        i.arg[P5](5),
        i.arg[P6](6),
        i.arg[P7](7),
        i.arg[P8](8),
        i.arg[P9](9),
        i.arg[P10](10),
        i.arg[P11](11),
        i.arg[P12](12),
        i.arg[P13](13),
        i.arg[P14](14),
        i.arg[P15](15),
        i.arg[P16](16),
        i.arg[P17](17),
        i.arg[P18](18),
        i.arg[P19](19),
        i.arg[P20](20),
        i.arg[P21](21)
      )
    )

//  (1 to 22).foreach { fn =>
//    val args = 0 until fn
//
//    print(s"""
//             |def functionToAnswer[T: ValueClassExtractor, ${args.map(a => s"P$a: ValueClassWrapper").mkString(",")}](f: (${args.map(a => s"P$a").mkString(",")}) => T): ScalaAnswer[T] =
//             |    invocationToAnswer(i => f(${args.map(a => s"i.arg[P$a]($a)").mkString(",")}))
//             |""".stripMargin)
//  }

  //Look at org.mockito.internal.invocation.InvocationMatcher#hasSameMethod
  implicit val JavaMethodEquality: Equality[Method] = new Equality[Method] {
    override def areEqual(m1: Method, b: Any): Boolean =
      b match {
        case m2: Method =>
          m1.getName === m2.getName && m1.getParameterTypes === m2.getParameterTypes
        case _ => false
      }
  }

  def serialisableEquality[T]: Equality[T] =
    new Equality[T] with Serializable {
      override def areEqual(a: T, b: Any): Boolean = Equality.default[T].areEqual(a, b)
    }
}
