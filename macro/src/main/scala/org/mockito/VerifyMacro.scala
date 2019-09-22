package org.mockito

import org.mockito.Utils._
import org.mockito.verification.VerificationMode

import scala.reflect.macros.blackbox

object Called {
  def by[T](stubbing: T): T = macro DoSomethingMacro.calledBy[T]
}

object VerifyMacro {

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

  object Never extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.never
  }

  object Once extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.times(1)
  }

  private def transformInvocation(c: blackbox.Context)(invocation: c.Tree, order: c.Tree, times: c.Tree): c.Tree = {
    import c.universe._

    val pf: PartialFunction[c.Tree, c.Tree] = {
      case q"$obj.$method[..$targs](...$args)" =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"verification($order.verifyWithMode($obj, $times).$method[..$targs](...$newArgs))"
      case q"$obj.$method[..$targs]" =>
        q"verification($order.verifyWithMode($obj, $times).$method[..$targs])"
    }

    if (pf.isDefinedAt(invocation))
      pf(invocation)
    else if (pf.isDefinedAt(invocation.children.last)) {
      val values = invocation.children
        .dropRight(1)
        .collect {
          case q"$_ val $name:$_ = $value" => name.toString -> value.asInstanceOf[c.Tree]
        }
        .toMap

      val nonMatchers = invocation.children.dropRight(1).collect {
        case t @ q"$_ val $_:$_ = $value" if !isMatcher(c)(value) => t
      }

      invocation.children.last match {
        case q"$obj.$method[..$targs](...$args)" =>
          val newArgs = args.map { a =>
            transformArgs(c)(a).map {
              case p if show(p).startsWith("x$") => transformArg(c)(values(p.toString))
              case other                         => other
            }
          }
          q"..$nonMatchers; verification($order.verifyWithMode($obj, $times).$method[..$targs](...$newArgs))"
      }
    } else throw new Exception(s"Couldn't recognize invocation ${show(invocation)}")
  }

  def transformVerification[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Tree): c.Tree = {
    import c.universe._

    called match {
      case q"$_.VerifyingOps[$_]($invocation).was($_.called)($order)" =>
        transformInvocation(c)(invocation, order, q"_root_.org.mockito.VerifyMacro.Once")

      case q"$_.VerifyingOps[$_]($_.this.$obj).wasNever($called)($_)" =>
        called match {
          case q"$_.called"      => q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"
          case q"$_.calledAgain" => q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"
          case q"$_.calledAgain.apply($_.ignoringStubs)" =>
            q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions(_root_.org.mockito.MockitoSugar.ignoreStubs($obj): _*))"
        }

      case q"$_.VerifyingOps[$_]($obj.$method[..$targs](...$args)).wasNever($_.called)($order)" =>
        transformInvocation(c)(q"$obj.$method[..$targs](...$args)", order, q"_root_.org.mockito.VerifyMacro.Never")

      case q"$_.VerifyingOps[$_]($obj).wasNever($called)($_)" =>
        called match {
          case q"$_.called"      => q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"
          case q"$_.calledAgain" => q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"
          case q"$_.calledAgain.apply($_.ignoringStubs)" =>
            q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions(_root_.org.mockito.MockitoSugar.ignoreStubs($obj): _*))"
        }

      case q"$_.VerifyingOps[$_]($invocation).wasCalled($times)($order)" =>
        transformInvocation(c)(invocation, order, times)

      case o => throw new Exception(s"VerifyMacro: Couldn't recognize ${show(o)}")
    }
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
