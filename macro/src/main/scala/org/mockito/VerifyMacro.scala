package org.mockito

import org.mockito.Utils._
import org.mockito.internal.MacroDebug.debugResult
import org.mockito.internal.verification.VerificationModeFactory
import org.mockito.verification.VerificationMode

import scala.reflect.macros.blackbox

object Called {
  def by[T](stubbing: T): T = macro DoSomethingMacro.calledBy[T]
}

object VerifyMacro extends VerificationMacroTransformer {
  def wasMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Tree)(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    val r = c.Expr[R](transformVerification(c)(c.macroApplication))
    debugResult(c)("mockito-print-verify")(r.tree)
    r
  }

  def wasNeverCalledAgainMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Tree)($ev: c.Tree): c.Expr[R] = {
    val r = c.Expr[R](transformVerification(c)(c.macroApplication))
    debugResult(c)("mockito-print-verify")(r.tree)
    r
  }

  object Never extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.never
  }

  object NeverAgain extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = VerificationModeFactory.noMoreInteractions()
  }

  object Once extends ScalaVerificationMode {
    override def verificationMode: VerificationMode = Mockito.times(1)
  }

}

private[mockito] trait VerificationMacroTransformer {
  protected def transformInvocation(c: blackbox.Context)(invocation: c.Tree, order: c.Tree, times: c.Tree): c.Tree = {
    import c.universe._

    try doTransformInvocation(c)(invocation, order, times)
    catch {
      case e: Exception => throw new Exception(s"Error when transforming invocation ${show(invocation)}", e)
    }
  }

  protected def doTransformInvocation(c: blackbox.Context)(invocation: c.Tree, order: c.Tree, times: c.Tree): c.Tree = {
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
    else if (invocation.children.nonEmpty && pf.isDefinedAt(invocation.children.last)) {
      val vals = invocation.children.dropRight(1)
      val valsByName = vals.collect { case line @ q"$_ val $name:$_ = $value" =>
        name.toString -> (value.asInstanceOf[c.Tree], line)
      }.toMap

      val inlinedArgsCall = invocation.children.last match {
        case q"$obj.$method[..$targs](...$args)" =>
          val newArgs = args.map { a =>
            transformArgs(c)(a).map {
              case p if show(p).startsWith("x$") => transformArg(c)(valsByName(p.toString)._1)
              case other                         => other
            }
          }
          q"verification($order.verifyWithMode($obj, $times).$method[..$targs](...$newArgs))"
      }

      val call = show(inlinedArgsCall)
      val usedVals = valsByName.collect {
        case (name, (_, line)) if call.contains(name) => line
      }

      q"..$usedVals; $inlinedArgsCall"
    } else throw new Exception(s"Couldn't recognize invocation ${show(invocation)}")
  }

  protected def transformVerification[T: c.WeakTypeTag, R](c: blackbox.Context)(called: c.Tree): c.Tree = {
    import c.universe._

    def transformMockWasNeverCalled(obj: c.Tree, called: c.Tree): c.Tree =
      called match {
        case q"$_.called"      => q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"
        case q"$_.calledAgain" => q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"
        case q"$_.calledAgain.apply($_.ignoringStubs)" =>
          q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions(_root_.org.mockito.MockitoSugar.ignoreStubs($obj): _*))"
      }

    called match {
      case q"$_.VerifyingOps[$_]($invocation).was($_.called)($order)" =>
        transformInvocation(c)(invocation, order, q"_root_.org.mockito.VerifyMacro.Once")

      case q"$_.VerifyingOps[$_]($a.$b).wasNever($called)($order)" =>
        q"""
           if (_root_.org.mockito.MockitoSugar.mockingDetails($a).isMock) {
            ${called match {
            case q"$_.calledAgain" =>
              val calledPattern = show(q"$a.$b")
              q"""throw new _root_.org.mockito.exceptions.misusing.NotAMockException(Seq(
                "[" + $calledPattern + "] is not a mock!",
                "Example of correct verification:",
                "    myMock wasNever called",
                ""
              ).mkString("\n"))
             """
            case _ => transformInvocation(c)(q"$a.$b", order, q"_root_.org.mockito.VerifyMacro.Never")
          }}
          } else { 
            ${transformMockWasNeverCalled(q"$a.$b", called)}
          }
         """

      case q"$_.VerifyingOps[$_]($obj.$method[..$targs](...$args)).wasNever($_.called)($order)" =>
        transformInvocation(c)(q"$obj.$method[..$targs](...$args)", order, q"_root_.org.mockito.VerifyMacro.Never")

      case q"$_.VerifyingOps[$_]($obj).wasNever($called)($_)" =>
        transformMockWasNeverCalled(obj, called)

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
