package org.mockito

import org.mockito.stubbing.{ ScalaFirstStubbing, ScalaOngoingStubbing }
import org.mockito.Utils._

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.ClassTag

object WhenMacro {

  class ReturnActions[T](os: ScalaFirstStubbing[T]) {
    def apply(value: T): ScalaOngoingStubbing[T] = os thenReturn value
  }

  def shouldReturn[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[ReturnActions[T]] = {
    import c.universe._

    c.Expr[ReturnActions[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method[..$targs](..$args)).shouldReturn" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs](..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs](..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method[..$targs]).shouldReturn" =>
          q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs]))"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }

  }

  def shouldCallRealMethod[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[ScalaOngoingStubbing[T]] = {
    import c.universe._

    c.Expr[ScalaOngoingStubbing[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method[..$targs](..$args)).shouldCallRealMethod" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method[..$targs](..$newArgs)).thenCallRealMethod())"
          } else
            q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method[..$targs](..$args)).thenCallRealMethod())"

        case q"$_.StubbingOps[$t]($obj.$method[..$targs]).shouldCallRealMethod" =>
          q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method[..$targs]).thenCallRealMethod())"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }

  }

  class ThrowActions[T](os: ScalaFirstStubbing[T]) {
    def apply[E <: Throwable](e: E): ScalaOngoingStubbing[T] = os thenThrow e
  }

  def shouldThrow[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[ThrowActions[T]] = {
    import c.universe._

    c.Expr[ThrowActions[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method[..$targs](..$args)).shouldThrow" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs](..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs](..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method[..$targs]).shouldThrow" =>
          q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs]))"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }

  }

  class AnswerActions[T](os: ScalaFirstStubbing[T]) {
    def apply(f: => T): ScalaOngoingStubbing[T]                  = os thenAnswer f
    def apply[P0: ClassTag](f: P0 => T): ScalaOngoingStubbing[T] = os thenAnswer f
    def apply[P0, P1](f: (P0, P1) => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0, P1, P2](f: (P0, P1, P2) => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0, P1, P2, P3](f: (P0, P1, P2, P3) => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4](f: (P0, P1, P2, P3, P4) => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5](f: (P0, P1, P2, P3, P4, P5) => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6](f: (P0, P1, P2, P3, P4, P5, P6) => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7](f: (P0, P1, P2, P3, P4, P5, P6, P7) => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => T): ScalaOngoingStubbing[T] = os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f

    def apply[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => T): ScalaOngoingStubbing[T] =
      os thenAnswer f
  }

  def shouldAnswer[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[AnswerActions[T]] = {
    import c.universe._

    c.Expr[AnswerActions[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method[..$targs](..$args)).shouldAnswer" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs](..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs](..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method[..$targs]).shouldAnswer" =>
          q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method[..$targs]))"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }

  }
}
