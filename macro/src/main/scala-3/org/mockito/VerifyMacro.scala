package org.mockito

import org.mockito.Utils.*
import org.mockito.VerifyMacroRuntime.{ Never, NeverAgain, Once }

import scala.quoted.*

/**
 * Scala 3 macro implementations for the idiomatic verification DSL. Runtime support classes are in [[VerifyMacroRuntime]].
 */
object VerifyMacro {
  // Re-export runtime objects for backward compatibility
  val Never      = VerifyMacroRuntime.Never
  val NeverAgain = VerifyMacroRuntime.NeverAgain
  val Once       = VerifyMacroRuntime.Once

  /** Macro for: `mock.method(args) was called` */
  inline def wasMacro[T, R](inline stubbing: T, inline called: Any)(using inline order: VerifyOrder): R =
    ${ wasMacroImpl[T, R]('stubbing, 'called, 'order) }

  def wasMacroImpl[T: Type, R: Type](stubbing: Expr[T], called: Expr[Any], order: Expr[VerifyOrder])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    transformVerification(stubbing.asTerm, order.asTerm, '{ VerifyMacroRuntime.Once }.asTerm).asExprOf[R]
  }

  /** Macro for: `mock.method(args) wasNever called` or `mock wasNever called` */
  inline def wasNeverMacro[T, R](inline stubbing: T, inline called: Any)(using inline order: VerifyOrder): R =
    ${ wasNeverMacroImpl[T, R]('stubbing, 'called, 'order) }

  def wasNeverMacroImpl[T: Type, R: Type](stubbing: Expr[T], called: Expr[Any], order: Expr[VerifyOrder])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    val invocation = stubbing.asTerm
    if (isMethodInvocation(invocation)) {
      // Method call: mock.method() wasNever called
      transformVerification(invocation, order.asTerm, '{ VerifyMacroRuntime.Never }.asTerm).asExprOf[R]
    } else {
      // Mock object: mock wasNever called → verifyNoInteractions
      val mockExpr   = stubbing.asExprOf[AnyRef]
      val verifyCall = '{ org.mockito.Mockito.verifyNoInteractions($mockExpr) }.asTerm
      wrapInVerification(verifyCall).asExprOf[R]
    }
  }

  /** Macro for: `mock.method(args) wasCalled times(n)` */
  inline def wasCalledMacro[T, R](inline stubbing: T, inline times: ScalaVerificationMode)(using inline order: VerifyOrder): R =
    ${ wasCalledMacroImpl[T, R]('stubbing, 'times, 'order) }

  def wasCalledMacroImpl[T: Type, R: Type](stubbing: Expr[T], times: Expr[ScalaVerificationMode], order: Expr[VerifyOrder])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    transformVerification(stubbing.asTerm, order.asTerm, times.asTerm).asExprOf[R]
  }

  /** Macro for: `mock wasNever calledAgain` */
  inline def wasNeverCalledAgainMacro[T, R](inline stubbing: T, inline called: Any): R =
    ${ wasNeverCalledAgainMacroImpl[T, R]('stubbing, 'called) }

  def wasNeverCalledAgainMacroImpl[T: Type, R: Type](stubbing: Expr[T], called: Expr[Any])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    val mockExpr = stubbing.asExprOf[AnyRef]
    val exprStr  = Expr(stubbing.show)

    val verifyBody: Expr[Unit] =
      if (detectIgnoringStubs(called.asTerm)) {
        '{ org.mockito.Mockito.ignoreStubs($mockExpr); org.mockito.Mockito.verifyNoMoreInteractions($mockExpr) }
      } else {
        '{ org.mockito.Mockito.verifyNoMoreInteractions($mockExpr) }
      }

    val wrappedCall = '{
      try $verifyBody
      catch {
        case _: org.mockito.exceptions.misusing.NotAMockException =>
          throw new org.mockito.exceptions.misusing.NotAMockException(
            s"[${$exprStr}] is not a mock!\nExample of correct verification:\n    myMock wasNever called\n"
          )
      }
    }
    wrapInVerification(wrappedCall.asTerm).asExprOf[R]
  }

  private def transformVerification(using Quotes)(invocation: quotes.reflect.Term, order: quotes.reflect.Term, times: quotes.reflect.Term): quotes.reflect.Term =
    hoistAndVerify(invocation, order, times)

  /** Detect if a `calledAgain(...)` expression is the lenient variant (i.e. `calledAgain(ignoringStubs)`) by inspecting the AST. */
  private def detectIgnoringStubs(using Quotes)(term: quotes.reflect.Term): Boolean = {
    import quotes.reflect.*
    term match {
      case Inlined(_, _, body)                                                                        => detectIgnoringStubs(body)
      case Block(_, expr)                                                                             => detectIgnoringStubs(expr)
      case sel: Select if sel.symbol.fullName.contains("LenientCalledAgain")                          => true
      case ident: Ident if ident.symbol.fullName.contains("LenientCalledAgain")                       => true
      case Apply(fun, _) if fun.symbol.fullName.contains("CalledAgain") && fun.symbol.name == "apply" => true
      case _                                                                                          => false
    }
  }
}
