package org.mockito

import org.mockito.Utils._
import org.mockito.verification.VerificationMode

import scala.reflect.macros.blackbox

object Called {
  def by[T](stubbing: T): T = macro DoSomethingMacro.calledBy[T]
}

object VerifyMacro {

  def wasMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Expr[Called.type])(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    import c.universe._

    val r = c.Expr[R] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method[..$targs](...$args)).was($_.called)($order)" =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"verification($order.verify($obj).$method[..$targs](...$newArgs))"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).was($_.called)($order)" =>
          q"verification($order.verify($obj).$method[..$targs])"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-verify")) println(show(r.tree))
    r
  }

  object Never extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.never
  }

  def wasNotMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Expr[Called.type])(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    import c.universe._

    val r = c.Expr[R] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($_.this.$obj).wasNever($_.called)($_)" =>
          q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs](...$args)).wasNever($_.called)($order)" =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"verification($order.verifyWithMode($obj, _root_.org.mockito.VerifyMacro.Never).$method[..$targs](...$newArgs))"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).wasNever($_.called)($order)" =>
          q"verification($order.verifyWithMode($obj, _root_.org.mockito.VerifyMacro.Never).$method[..$targs])"

        case q"$_.StubbingOps[$_]($obj).wasNever($_.called)($_)" =>
          q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-verify")) println(show(r.tree))
    r
  }

  def wasCalledMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(t: c.Expr[ScalaVerificationMode])(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    import c.universe._

    val r = c.Expr[R] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method[..$targs](...$args)).wasCalled($times)($order)" =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"verification($order.verifyWithMode($obj, $times).$method[..$targs](...$newArgs))"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).wasCalled($times)($order)" =>
          q"verification($order.verifyWithMode($obj, $times).$method[..$targs])"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-verify")) println(show(r.tree))
    r
  }
}

trait ScalaVerificationMode {
  def verificationMode: VerificationMode
}

sealed trait VerifyOrder {
  def verify[T](mock: T): T
  def verifyWithMode[T](mock: T, mode: ScalaVerificationMode): T
}

object VerifyUnOrdered extends VerifyOrder {
  override def verify[T](mock: T): T                                      = Mockito.verify(mock)
  override def verifyWithMode[T](mock: T, mode: ScalaVerificationMode): T = Mockito.verify(mock, mode.verificationMode)
}

case class VerifyInOrder(mocks: Seq[AnyRef]) extends VerifyOrder {
  private val _inOrder = Mockito.inOrder(mocks: _*)

  override def verify[T](mock: T): T                                      = _inOrder.verify(mock)
  override def verifyWithMode[T](mock: T, mode: ScalaVerificationMode): T = _inOrder.verify(mock, mode.verificationMode)
  def verifyNoMoreInteractions(): Unit                                    = _inOrder.verifyNoMoreInteractions()
}

object VerifyOrder {
  implicit val unOrdered: VerifyOrder = VerifyUnOrdered
}
