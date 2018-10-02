package org.mockito

import org.mockito.stubbing.{ScalaFirstStubbing, ScalaOngoingStubbing}

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox
import scala.reflect.ClassTag

object WhenMacro {

  private def isMatcher(c: blackbox.Context)(arg: c.Tree): Boolean = {
    import c.universe._
    if (arg.toString().contains("org.mockito.matchers.ValueClassMatchers")) true
    else
      arg match {
        case q"$_.anyList[$_]"     => true
        case q"$_.anySeq[$_]"      => true
        case q"$_.anyIterable[$_]" => true
        case q"$_.anySet[$_]"      => true
        case q"$_.anyMap[$_, $_]"  => true
        case q"$_.any[$_]"         => true
        case q"$_.*[$_]"           => true
        case q"$_.anyByte"         => true
        case q"$_.anyBoolean"      => true
        case q"$_.anyChar"         => true
        case q"$_.anyDouble"       => true
        case q"$_.anyInt"          => true
        case q"$_.anyFloat"        => true
        case q"$_.anyShort"        => true
        case q"$_.anyLong"         => true

        case q"$_.isNull[$_]"    => true
        case q"$_.isNotNull[$_]" => true

        case q"$_.eqTo[$_]($_)"      => true
        case q"$_.same[$_]($_)"      => true
        case q"$_.isA[$_]($_)"       => true
        case q"$_.refEq[$_]($_, $_)" => true

        case q"$_.function0[$_]($_)" => true

        case q"$_.matches[$_]($_)"    => true
        case q"$_.startsWith[$_]($_)" => true
        case q"$_.contains[$_]($_)"   => true
        case q"$_.endsWith[$_]($_)"   => true

        case q"$_.argThat[$_]($_)"     => true
        case q"$_.byteThat[$_]($_)"    => true
        case q"$_.booleanThat[$_]($_)" => true
        case q"$_.charThat[$_]($_)"    => true
        case q"$_.doubleThat[$_]($_)"  => true
        case q"$_.intThat[$_]($_)"     => true
        case q"$_.floatThat[$_]($_)"   => true
        case q"$_.shortThat[$_]($_)"   => true
        case q"$_.longThat[$_]($_)"    => true

        case _ => false
      }
  }

  private def transformArg(c: blackbox.Context)(arg: c.Tree): c.Tree = {
    import c.universe._
    if (isMatcher(c)(arg)) arg
    else
      arg match {
        case q"$a" => q"org.mockito.matchers.EqMatchers.eqTo($a)"
      }
  }

  class ReturnActions[T](os: ScalaFirstStubbing[T]) {
    def apply(value: T): ScalaOngoingStubbing[T] = os thenReturn value
  }

  def shouldReturn[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[ReturnActions[T]] = {
    import c.universe._

    c.Expr[ReturnActions[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method(..$args)).shouldReturn" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method(..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method(..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method[$targs](..$args)).shouldReturn" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method[$targs](..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method[$targs](..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method).shouldReturn" =>
          q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method))"

        case q"$_.StubbingOps[$t]($obj.$method[$targs]).shouldReturn" =>
          q"new org.mockito.WhenMacro.ReturnActions(org.mockito.Mockito.when[$t]($obj.$method[$targs]))"

        case o =>
          println("other", show(o))
          ???
      }
    }

  }

  def shouldCallRealMethod[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[ScalaOngoingStubbing[T]] = {
    import c.universe._

    c.Expr[ScalaOngoingStubbing[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method(..$args)).shouldCallRealMethod" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method(..$newArgs)).thenCallRealMethod())"
          } else
            q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method(..$args)).thenCallRealMethod())"

        case q"$_.StubbingOps[$t]($obj.$method[$targs](..$args)).shouldCallRealMethod" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method[$targs](..$newArgs)).thenCallRealMethod())"
          } else
            q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method[$targs](..$args)).thenCallRealMethod())"

        case q"$_.StubbingOps[$t]($obj.$method).shouldCallRealMethod" =>
          q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method).thenCallRealMethod())"

        case q"$_.StubbingOps[$t]($obj.$method[$targs]).shouldCallRealMethod" =>
          q"new org.mockito.stubbing.ScalaOngoingStubbing(org.mockito.Mockito.when[$t]($obj.$method[$targs]).thenCallRealMethod())"

        case o =>
          println("other", show(o))
          ???
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
        case q"$_.StubbingOps[$t]($obj.$method(..$args)).shouldThrow" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method(..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method(..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method[$targs](..$args)).shouldThrow" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method[$targs](..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method[$targs](..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method).shouldThrow" =>
          q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method))"

        case q"$_.StubbingOps[$t]($obj.$method[$targs]).shouldThrow" =>
          q"new org.mockito.WhenMacro.ThrowActions(org.mockito.Mockito.when[$t]($obj.$method[$targs]))"

        case o =>
          println("other", show(o))
          ???
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
        case q"$_.StubbingOps[$t]($obj.$method(..$args)).shouldAnswer" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method(..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method(..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method[$targs](..$args)).shouldAnswer" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method[$targs](..$newArgs)))"
          } else
            q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method[$targs](..$args)))"

        case q"$_.StubbingOps[$t]($obj.$method).shouldAnswer" =>
          q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method))"

        case q"$_.StubbingOps[$t]($obj.$method[$targs]).shouldAnswer" =>
          q"new org.mockito.WhenMacro.AnswerActions(org.mockito.Mockito.when[$t]($obj.$method[$targs]))"

        case o =>
          println("other", show(o))
          ???
      }
    }

  }

  def traditionalWhen[T: c.WeakTypeTag](c: blackbox.Context)(expr: c.Expr[T]): c.Expr[ScalaFirstStubbing[T]] = {
    import c.universe._

    val t = weakTypeOf[T]

    c.Expr[ScalaFirstStubbing[T]] {
      expr.tree match {
        case q"$obj.$method(..$args)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method(..$newArgs)))"
          } else
            q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method(..$args)))"

        case q"$obj.$method[..$tagrs](..$args)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method[..$tagrs](..$newArgs)))"
          } else
            q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method[..$tagrs](..$args)))"

        case q"$obj.$method" => q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method))"

        case q"$obj.$method[..$tagrs]" => q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[..$tagrs]($obj.$method))"

        case o =>
          println("other", show(o))
          ???
      }
    }
  }

}
