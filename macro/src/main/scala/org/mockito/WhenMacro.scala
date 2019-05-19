package org.mockito

import org.mockito.Utils._
import org.mockito.stubbing.{ ScalaFirstStubbing, ScalaOngoingStubbing }

import scala.reflect.ClassTag
import scala.reflect.macros.blackbox

object WhenMacro {

  private val ShouldReturnOptions      = Set("shouldReturn", "mustReturn", "returns")
  private val CatsShouldReturnOptions  = ShouldReturnOptions.map(_ + "F")
  private val CatsShouldReturnOptions2 = ShouldReturnOptions.map(_ + "FG")
  def shouldReturn[T: c.WeakTypeTag](c: blackbox.Context): c.Tree = {
    import c.universe._

    val r = c.macroApplication match {
      case q"$_.StubbingOps[$t]($obj.$method[..$targs](...$args)).$m" if ShouldReturnOptions.contains(m.toString) =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"new _root_.org.mockito.IdiomaticMockitoBase.ReturnActions(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs](...$newArgs)))"

      case q"$_.StubbingOps[$t]($obj.$method[..$targs]).$m" if ShouldReturnOptions.contains(m.toString) =>
        q"new _root_.org.mockito.IdiomaticMockitoBase.ReturnActions(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs]))"

      case q"$_.StubbingOps[..$_]($obj.$method[..$targs](...$args)).$m" if CatsShouldReturnOptions.contains(m.toString) =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"new _root_.org.mockito.cats.IdiomaticMockitoCats.ReturnActions(_root_.org.mockito.Mockito.when($obj.$method[..$targs](...$newArgs)))"

      case q"$_.StubbingOps[..$_]($obj.$method[..$targs]).$m" if CatsShouldReturnOptions.contains(m.toString) =>
        q"new _root_.org.mockito.cats.IdiomaticMockitoCats.ReturnActions(_root_.org.mockito.Mockito.when($obj.$method[..$targs]))"

      case q"$_.StubbingOps2[..$_]($obj.$method[..$targs](...$args)).$m" if CatsShouldReturnOptions2.contains(m.toString) =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"new _root_.org.mockito.cats.IdiomaticMockitoCats.ReturnActions2(_root_.org.mockito.Mockito.when($obj.$method[..$targs](...$newArgs)))"

      case q"$_.StubbingOps2[..$_]($obj.$method[..$targs]).$m" if CatsShouldReturnOptions2.contains(m.toString) =>
        q"new _root_.org.mockito.cats.IdiomaticMockitoCats.ReturnActions2(_root_.org.mockito.Mockito.when($obj.$method[..$targs]))"

      case o => throw new Exception(s"Couldn't recognize ${show(o)}")
    }
    if (c.settings.contains("mockito-print-when")) println(show(r))
    r
  }

  def isLenient[T: c.WeakTypeTag](c: blackbox.Context)(): c.Expr[Unit] = {
    import c.universe._

    val r = c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method[..$targs](...$args)).isLenient()" =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"new _root_.org.mockito.stubbing.ScalaFirstStubbing(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs](...$newArgs))).isLenient()"

        case q"$_.StubbingOps[$t]($obj.$method[..$targs]).isLenient()" =>
          q"new _root_.org.mockito.stubbing.ScalaFirstStubbing(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs])).isLenient()"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-lenient")) println(show(r.tree))
    r
  }

  object RealMethod {
    def willBe(called: Called.type): Called.type = called
  }

  val ShouldCallOptions = Set("shouldCall", "mustCall", "calls")
  def shouldCallRealMethod[T: c.WeakTypeTag](c: blackbox.Context)(crm: c.Expr[RealMethod.type]): c.Expr[ScalaOngoingStubbing[T]] = {
    import c.universe._

    val r = c.Expr[ScalaOngoingStubbing[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method[..$targs](...$args)).$m($_.realMethod)" if ShouldCallOptions.contains(m.toString) =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"new _root_.org.mockito.stubbing.ScalaOngoingStubbing(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs](...$newArgs)).thenCallRealMethod())"

        case q"$_.StubbingOps[$t]($obj.$method[..$targs]).$m($_.realMethod)" if ShouldCallOptions.contains(m.toString) =>
          q"new _root_.org.mockito.stubbing.ScalaOngoingStubbing(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs]).thenCallRealMethod())"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-when")) println(show(r.tree))
    r
  }

  private val ShouldThrowOptions     = Set("shouldThrow", "mustThrow", "throws")
  private val CatsShouldFailOptions  = Set("shouldFailWith", "mustFailWith", "failsWith")
  private val CatsShouldFailOptions2 = Set("shouldFailWithG", "mustFailWithG", "failsWithG")
  def shouldThrow[T: c.WeakTypeTag](c: blackbox.Context): c.Tree = {
    import c.universe._

    val r = c.macroApplication match {
      case q"$_.StubbingOps[$t]($obj.$method[..$targs](...$args)).$m" if ShouldThrowOptions.contains(m.toString) =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"new _root_.org.mockito.IdiomaticMockitoBase.ThrowActions(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs](...$newArgs)))"

      case q"$_.StubbingOps[$t]($obj.$method[..$targs]).$m" if ShouldThrowOptions.contains(m.toString) =>
        q"new _root_.org.mockito.IdiomaticMockitoBase.ThrowActions(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs]))"

      case q"$_.StubbingOps[..$_]($obj.$method[..$targs](...$args)).$m" if CatsShouldFailOptions.contains(m.toString) =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"new _root_.org.mockito.cats.IdiomaticMockitoCats.ThrowActions(_root_.org.mockito.Mockito.when($obj.$method[..$targs](...$newArgs)))"

      case q"$_.StubbingOps[..$_]($obj.$method[..$targs]).$m" if CatsShouldFailOptions.contains(m.toString) =>
        q"new _root_.org.mockito.cats.IdiomaticMockitoCats.ThrowActions(_root_.org.mockito.Mockito.when($obj.$method[..$targs]))"

      case q"$_.StubbingOps2[..$_]($obj.$method[..$targs](...$args)).$m" if CatsShouldFailOptions2.contains(m.toString) =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"new _root_.org.mockito.cats.IdiomaticMockitoCats.ThrowActions2(_root_.org.mockito.Mockito.when($obj.$method[..$targs](...$newArgs)))"

      case q"$_.StubbingOps2[..$_]($obj.$method[..$targs]).$m" if CatsShouldFailOptions2.contains(m.toString) =>
        q"new _root_.org.mockito.cats.IdiomaticMockitoCats.ThrowActions2(_root_.org.mockito.Mockito.when($obj.$method[..$targs]))"

      case o => throw new Exception(s"Couldn't recognize ${show(o)}")
    }
    if (c.settings.contains("mockito-print-when")) println(show(r))
    r
  }

  class AnswerActions[T](os: ScalaFirstStubbing[T]) {
    def apply(f: => T): ScalaOngoingStubbing[T] = os thenAnswer f

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

  val ShouldAnswerOptions = Set("shouldAnswer", "mustAnswer", "answers")
  def shouldAnswer[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[AnswerActions[T]] = {
    import c.universe._

    val r = c.Expr[AnswerActions[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($obj.$method[..$targs](...$args)).$m" if ShouldAnswerOptions.contains(m.toString) =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"new _root_.org.mockito.WhenMacro.AnswerActions(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs](...$newArgs)))"

        case q"$_.StubbingOps[$t]($obj.$method[..$targs]).$m" if ShouldAnswerOptions.contains(m.toString) =>
          q"new _root_.org.mockito.WhenMacro.AnswerActions(_root_.org.mockito.Mockito.when[$t]($obj.$method[..$targs]))"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-when")) println(show(r.tree))
    r
  }
}
