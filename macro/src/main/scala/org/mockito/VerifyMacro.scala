package org.mockito

import org.mockito.Utils._
import org.mockito.verification.VerificationMode

import scala.reflect.macros.blackbox

object Called {
  def by[T](stubbing: T): T = macro DoSomethingMacro.calledBy[T]
}

object VerifyMacro {

  object Never extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.never
  }

  def transformVerification[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Tree): c.Tree = {
    import c.universe._

    called match {
      case q"$_.VerifyingOps[$_]($obj.$method[..$targs](...$args)).was($_.called)($order)" =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"verification($order.verify($obj).$method[..$targs](...$newArgs))"

      case q"$_.VerifyingOps[$_]($obj.$method[..$targs]).was($_.called)($order)" =>
        q"verification($order.verify($obj).$method[..$targs])"

      case q"$_.VerifyingOps[$_]($_.this.$obj).wasNever($called)($_)" =>
        called match {
          case q"$_.called"      => q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"
          case q"$_.calledAgain" => q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"
          case q"$_.calledAgain.apply($_.ignoringStubs)" =>
            q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions(_root_.org.mockito.MockitoSugar.ignoreStubs($obj): _*))"
        }

      case q"$_.VerifyingOps[$_]($obj.$method[..$targs](...$args)).wasNever($_.called)($order)" =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"verification($order.verifyWithMode($obj, _root_.org.mockito.VerifyMacro.Never).$method[..$targs](...$newArgs))"

      case q"$_.VerifyingOps[$_]($obj.$method[..$targs]).wasNever($_.called)($order)" =>
        q"verification($order.verifyWithMode($obj, _root_.org.mockito.VerifyMacro.Never).$method[..$targs])"

      case q"$_.VerifyingOps[$_]($obj).wasNever($called)($_)" =>
        called match {
          case q"$_.called"      => q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"
          case q"$_.calledAgain" => q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"
          case q"$_.calledAgain.apply($_.ignoringStubs)" =>
            q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions(_root_.org.mockito.MockitoSugar.ignoreStubs($obj): _*))"
        }

      case q"$_.VerifyingOps[$_]($obj.$method[..$targs](...$args)).wasCalled($times)($order)" =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"verification($order.verifyWithMode($obj, $times).$method[..$targs](...$newArgs))"

      case q"$_.VerifyingOps[$_]($obj.$method[..$targs]).wasCalled($times)($order)" =>
        q"verification($order.verifyWithMode($obj, $times).$method[..$targs])"

      case o => throw new Exception(s"VerifyMacro: Couldn't recognize ${show(o)}")
    }
  }

  def wasMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Tree)(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    import c.universe._

    val r = c.Expr[R](transformVerification(c)(c.macroApplication))
    if (c.settings.contains("mockito-print-verify")) println(show(r.tree))
    r
  }

  def wasNeverCalledAgainMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Tree)($ev: c.Tree): c.Expr[R] = {
    import c.universe._

    val r = c.Expr[R](transformVerification(c)(c.macroApplication))
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
