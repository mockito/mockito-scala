package org.mockito

import org.mockito.internal.ValueClassWrapper
import org.mockito.stubbing.{ ScalaFirstStubbing, ScalaOngoingStubbing }

import scala.reflect.ClassTag

/**
 * Runtime support classes for WhenMacro. These classes are used by macro-generated code and shared across Scala 2 and Scala 3.
 */
object WhenMacroRuntime {

  object RealMethod {
    def willBe(called: Called.type): Called.type = called
  }

  class AnswerActions[T](os: ScalaFirstStubbing[T]) {
    def apply(f: => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0: ValueClassWrapper](f: P0 => T)(implicit classTag: ClassTag[P0] = defaultClassTag[P0]): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[P0: ValueClassWrapper, P1: ValueClassWrapper](f: (P0, P1) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper](f: (P0, P1, P2) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper](f: (P0, P1, P2, P3) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper](
        f: (P0, P1, P2, P3, P4) => T
    ): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper, P5: ValueClassWrapper](
        f: (P0, P1, P2, P3, P4, P5) => T
    ): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[P0: ValueClassWrapper, P1: ValueClassWrapper, P2: ValueClassWrapper, P3: ValueClassWrapper, P4: ValueClassWrapper, P5: ValueClassWrapper, P6: ValueClassWrapper](
        f: (P0, P1, P2, P3, P4, P5, P6) => T
    ): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
        P0: ValueClassWrapper,
        P1: ValueClassWrapper,
        P2: ValueClassWrapper,
        P3: ValueClassWrapper,
        P4: ValueClassWrapper,
        P5: ValueClassWrapper,
        P6: ValueClassWrapper,
        P7: ValueClassWrapper
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
        P0: ValueClassWrapper,
        P1: ValueClassWrapper,
        P2: ValueClassWrapper,
        P3: ValueClassWrapper,
        P4: ValueClassWrapper,
        P5: ValueClassWrapper,
        P6: ValueClassWrapper,
        P7: ValueClassWrapper,
        P8: ValueClassWrapper
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[
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
    ](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f
  }

  class AnswerPFActions[T](os: ScalaFirstStubbing[T]) {
    def apply(pf: PartialFunction[Any, T]): ScalaOngoingStubbing[T] = os thenAnswer pf
  }
}
