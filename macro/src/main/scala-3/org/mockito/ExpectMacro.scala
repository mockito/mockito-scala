package org.mockito

import org.mockito.Utils.*

import scala.collection.mutable
import scala.quoted.*

/**
 * Scala 3 macro implementations for the prefix expectations DSL (e.g., `expect a call to mock.method(args)`). Runtime support traits are in [[PrefixExpectationsRuntime]].
 */
object ExpectMacro {

  inline def callsTo[R](inline stubbedMethodCall: Any, mode: ScalaVerificationMode)(inline order: VerifyOrder): R =
    ${ callsToImpl[R]('stubbedMethodCall, 'mode, 'order) }

  def callsToImpl[R: Type](stubbedMethodCall: Expr[Any], mode: Expr[ScalaVerificationMode], order: Expr[VerifyOrder])(using Quotes): Expr[R] = {
    import quotes.reflect.*

    val application = stubbedMethodCall.asTerm
    val unwrapped   = stripWrappers(application)

    // Guard: if the user passed a mock object instead of a method call, emit a runtime error
    unwrapped match {
      case Select(obj, _) if !unwrapped.symbol.isDefDef =>
        val exprStr = Expr(stubbedMethodCall.show)
        val objExpr = obj.asExprOf[Any]
        val guard   = '{
          if (!org.mockito.Mockito.mockingDetails($objExpr.asInstanceOf[AnyRef]).isMock)
            throw new org.mockito.exceptions.misusing.MissingMethodInvocationException(
              s"'expect no calls to <?>' requires an argument which is 'a method call on a mock',\n" +
                s"  but looks like [${$exprStr}] is not a method call on a mock. Is it a mock object?\n\n" +
                "The following would be correct (note the usage of 'calls to' vs 'calls on'):\n" +
                "    expect no calls to aMock.bar(*)\n" +
                "    expect no calls on aMock\n"
            )
        }
        Block(List(guard.asTerm), transformExpectation(application, order, mode)).asExprOf[R]
      case _ =>
        transformExpectation(application, order, mode).asExprOf[R]
    }
  }

  inline def callsOn[R](inline mock: Any): R =
    ${ callsOnImpl[R]('mock, '{ false }, '{ false }) }

  inline def callsOnNoMore[R](inline mock: Any, inline ignoringStubs: Boolean): R =
    ${ callsOnImpl[R]('mock, '{ true }, 'ignoringStubs) }

  def callsOnImpl[R: Type](mock: Expr[Any], noMore: Expr[Boolean], ignoringStubs: Expr[Boolean])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    val mockTerm   = mock.asTerm
    val isIgnoring = ignoringStubs.value.getOrElse(false)
    val isNoMore   = noMore.value.getOrElse(false)
    (isNoMore, isIgnoring) match {
      case (true, true) => transformNoMoreInteractionsIgnoringStubsExpectation(mockTerm).asExprOf[R]
      case (true, _)    => transformNoMoreInteractionsExpectation(mockTerm).asExprOf[R]
      case _            => transformNoInteractionsExpectation(mockTerm).asExprOf[R]
    }
  }

  /** Generates runtime-branching code for `noMore calls` when `ignoringStubs` is a runtime value */
  def callsOnNoMoreImpl[R: Type](mock: Expr[Any], ignoringStubs: Expr[Boolean])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    ignoringStubs.value match {
      case Some(true)  => transformNoMoreInteractionsIgnoringStubsExpectation(mock.asTerm).asExprOf[R]
      case Some(false) => transformNoMoreInteractionsExpectation(mock.asTerm).asExprOf[R]
      case None        =>
        val noMoreExpr   = transformNoMoreInteractionsExpectation(mock.asTerm)
        val ignoringExpr = transformNoMoreInteractionsIgnoringStubsExpectation(mock.asTerm)
        If(ignoringStubs.asTerm, ignoringExpr, noMoreExpr).asExprOf[R]
    }
  }

  /** Macro for `noMore(calls)` / `noMore(calls(ignoringStubs))` — constructs the class with the flag */
  def noMoreMacro[R: Type](using Quotes)(callsExpr: Expr[Any]): Expr[R] = {
    import quotes.reflect.*
    val isIgnoring = detectIgnoringStubsInCalls(callsExpr.asTerm)
    val boolLit    = Literal(BooleanConstant(isIgnoring))
    val cls        = TypeRepr.of[R].typeSymbol
    Apply(Select(New(TypeTree.of[R]), cls.primaryConstructor), List(boolLit)).asExprOf[R]
  }

  /** Detect if a `CallsWord` expression was obtained via `calls(ignoringStubs)` */
  def detectIgnoringStubsMacro(using Quotes)(callsExpr: Expr[Any]): Expr[Boolean] = {
    import quotes.reflect.*
    Expr(detectIgnoringStubsInCalls(callsExpr.asTerm))
  }

  /** Strip `Inlined` and empty `Block(Nil, expr)` wrappers from a term */
  private def stripWrappers(using Quotes)(t: quotes.reflect.Term): quotes.reflect.Term = {
    import quotes.reflect.*
    t match {
      case Inlined(_, _, body) => stripWrappers(body)
      case Block(Nil, expr)    => stripWrappers(expr)
      case other               => other
    }
  }

  /** Build `order.verifyWithMode[ObjType](obj, times)` — replaces the mock object with a verifying proxy */
  private def buildVerifiedObj(using Quotes)(obj: quotes.reflect.Term, order: quotes.reflect.Term, times: quotes.reflect.Term): quotes.reflect.Term = {
    import quotes.reflect.*
    val objType = obj.tpe.widen.asType
    Apply(
      TypeApply(Select.unique(order, "verifyWithMode"), List(TypeTree.of(using objType))),
      List(obj, times)
    )
  }

  /**
   * Collect hoisted statements, transform the method invocation through `order.verifyWithMode`, wrap in `verification(...)`, and prepend any hoisted bindings.
   */
  private def transformExpectation(using
      Quotes
  )(
      invocation: quotes.reflect.Term,
      order: Expr[VerifyOrder],
      mode: Expr[ScalaVerificationMode]
  ): quotes.reflect.Term = {
    import quotes.reflect.*
    val hoisted    = mutable.ListBuffer.empty[Statement]
    val verifyCall = transformInvocation(invocation, order.asTerm, mode.asTerm, hoisted)
    val verifyExpr = wrapInVerification(verifyCall)
    if (hoisted.nonEmpty) Block(hoisted.toList, verifyExpr) else verifyExpr
  }

  /**
   * Transform invocation: `obj.method(args)` → `order.verifyWithMode(obj, mode).method(transformedArgs)`
   */
  private def transformInvocation(using
      Quotes
  )(
      invocation: quotes.reflect.Term,
      order: quotes.reflect.Term,
      times: quotes.reflect.Term,
      hoisted: mutable.ListBuffer[quotes.reflect.Statement],
      matcherValNames: Set[String] = Set.empty
  ): quotes.reflect.Term = {
    import quotes.reflect.*

    invocation match {
      case Block(stats, expr) =>
        transformBlock(stats, expr, matcherValNames)((e, mvs) => transformInvocation(e, order, times, hoisted, mvs))

      case inlined: Inlined =>
        transformInvocation(inlined.body, order, times, hoisted, matcherValNames)

      case Apply(select @ Select(obj, _), args) =>
        Apply(
          Select(buildVerifiedObj(obj, order, times), select.symbol),
          transformArgsForApply(select, args, hoisted, matcherValNames)
        )

      case Apply(TypeApply(select @ Select(obj, _), targs), args) =>
        Apply(
          TypeApply(Select(buildVerifiedObj(obj, order, times), select.symbol), targs),
          transformArgsForApply(TypeApply(select, targs), args, hoisted, matcherValNames)
        )

      case select @ Select(obj, _) =>
        Select(buildVerifiedObj(obj, order, times), select.symbol)

      case TypeApply(select @ Select(obj, _), targs) =>
        TypeApply(Select(buildVerifiedObj(obj, order, times), select.symbol), targs)

      case Apply(fun, args) =>
        Apply(
          transformInvocation(fun, order, times, hoisted, matcherValNames),
          transformArgsForApply(fun, args, hoisted, matcherValNames)
        )

      case other =>
        report.errorAndAbort(s"Could not transform expect invocation: ${other.show}")
    }
  }

  private def transformNoInteractionsExpectation(using Quotes)(mock: quotes.reflect.Term): quotes.reflect.Term = {
    import quotes.reflect.*
    val mockExpr = mock.asExprOf[AnyRef]
    wrapInVerification('{ org.mockito.Mockito.verifyNoInteractions($mockExpr) }.asTerm)
  }

  private def transformNoMoreInteractionsExpectation(using Quotes)(mock: quotes.reflect.Term): quotes.reflect.Term = {
    import quotes.reflect.*
    val mockExpr = mock.asExprOf[AnyRef]
    wrapInVerification('{ org.mockito.Mockito.verifyNoMoreInteractions($mockExpr) }.asTerm)
  }

  private def transformNoMoreInteractionsIgnoringStubsExpectation(using Quotes)(mock: quotes.reflect.Term): quotes.reflect.Term = {
    import quotes.reflect.*
    val mockExpr = mock.asExprOf[AnyRef]
    wrapInVerification('{ org.mockito.Mockito.ignoreStubs($mockExpr); org.mockito.Mockito.verifyNoMoreInteractions($mockExpr) }.asTerm)
  }

  private def detectIgnoringStubsInCalls(using Quotes)(term: quotes.reflect.Term): Boolean = {
    import quotes.reflect.*
    term match {
      case Inlined(_, _, body) => detectIgnoringStubsInCalls(body)
      case Block(_, expr)      => detectIgnoringStubsInCalls(expr)
      case Apply(_, args)      =>
        args.exists { a =>
          val shown = a.show
          shown.contains("ignoringStubs") || shown.contains("IgnoringStubs")
        }
      case _ => false
    }
  }
}
