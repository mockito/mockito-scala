package org.mockito

import org.mockito.Utils.*
import org.mockito.stubbing.{ ScalaAnswer, Stubber }
import scala.collection.mutable
import scala.quoted.*

/**
 * Scala 3 macro implementations for "do something by" DSL (e.g., `Returned(value) by mock.method(args)`).
 *
 * High-level flow:
 *   1. Take a user DSL form (`doesNothing`, `returnedBy`, `answeredBy`, `thrownBy`, `calledBy`).
 *   2. Normalize invocation arguments through shared tree transformers from [[Utils]].
 *   3. Emit the corresponding Mockito runtime action (`doNothing`, `doReturn`, `doAnswer`, `doCallRealMethod`).
 */
object DoSomethingMacro {

  /**
   * Generic macro: takes a pre-built Stubber and an invocation, transforms args. Used by cats/scalaz modules to implement returnedF/answeredF/raised etc.
   */
  inline def doSomethingBy[T](inline action: Stubber, inline stubbing: T): T =
    ${ doSomethingByImpl[T]('action, 'stubbing) }

  def doSomethingByImpl[T: Type](action: Expr[Stubber], stubbing: Expr[T])(using Quotes): Expr[T] = {
    import quotes.reflect.*
    doTransformInvocation[T](stubbing, action.asTerm)
  }

  /**
   * Macro for: mock.method(args) doesNothing
   */
  inline def doesNothing[T](inline stubbing: => T): T =
    ${ doesNothingImpl[T]('stubbing) }

  def doesNothingImpl[T: Type](stubbing: Expr[T])(using Quotes): Expr[T] = {
    import quotes.reflect.*

    // Use quoted expression to call Mockito.doNothing()
    val doNothingCall = '{ Mockito.doNothing() }.asTerm

    doTransformInvocation[T](stubbing, doNothingCall)
  }

  /**
   * Macro for: Returned(value) by mock.method(args)
   */
  inline def returnedBy[T, S](inline v: T, inline stubbing: S)(using inline $ev: T <:< S): S =
    ${ returnedByImpl[T, S]('v, 'stubbing) }

  // Wrapper for case class usage
  inline def returnedByMacro[T, S](inline v: T, inline stubbing: S): S =
    ${ returnedByImpl[T, S]('v, 'stubbing) }

  def returnedByImpl[T: Type, S: Type](v: Expr[T], stubbing: Expr[S])(using Quotes): Expr[S] = {
    import quotes.reflect.*

    // Type safety check: the return value type T must be assignable to the stubbed method's return type S
    if (!(TypeRepr.of[T] <:< TypeRepr.of[S])) report.errorAndAbort(s"Type mismatch: value of type ${TypeRepr.of[T].show} is not a subtype of ${TypeRepr.of[S].show}")

    // Explicitly type the doReturn result as Stubber to help type inference in transformInvocation.
    // For value classes, extract the underlying value before passing to Mockito.
    val doReturnCall = Typed(
      '{ org.mockito.Mockito.doReturn(org.mockito.internal.ValueClassExtractor[T].extract($v)) }.asTerm,
      TypeTree.of[Stubber]
    )

    doTransformInvocation[S](stubbing, doReturnCall)
  }

  /**
   * Macro for: Answered(func) by mock.method(args)
   */
  inline def answeredBy[T, S](inline v: T, inline stubbing: S)(using inline $ev: T <:< S): S =
    ${ answeredByImpl[T, S]('v, 'stubbing) }

  // Wrapper for case class usage
  inline def answeredByMacro[T, S](inline v: T, inline stubbing: S): S =
    ${ answeredByImpl[T, S]('v, 'stubbing) }

  // Thunk-based wrapper: value is deferred via () => T to prevent eager evaluation
  inline def answeredByThunkMacro[T, S](inline v: () => T, inline stubbing: S): S =
    ${ answeredByThunkImpl[T, S]('v, 'stubbing) }

  // Thunk variant: calling v() on each mock invocation gives the same generated code as passing the expression
  // directly, so we delegate to answeredByImpl with the thunk unwrapped.
  def answeredByThunkImpl[T: Type, S: Type](v: Expr[() => T], stubbing: Expr[S])(using Quotes): Expr[S] =
    answeredByImpl[T, S]('{ $v() }, stubbing)

  def answeredByImpl[T: Type, S: Type](v: Expr[T], stubbing: Expr[S])(using Quotes): Expr[S] = {
    import quotes.reflect.*

    val tRepr        = TypeRepr.of[T].dealias.widen
    val sRepr        = TypeRepr.of[S].dealias.widen
    val functionInfo = extractFunctionInfo(tRepr)
    val resultType   = functionInfo.map(_._2).getOrElse(tRepr)
    if (!(resultType <:< sRepr)) report.errorAndAbort(s"Type mismatch: answer result type ${resultType.show} is not a subtype of ${sRepr.show}")

    val doAnswerCall = functionInfo match {
      case Some((paramTypes, retType)) =>
        // Function type: extract args from InvocationOnMock and apply the function
        val answerExpr = buildFunctionAnswer(v, paramTypes, retType)
        '{ org.mockito.Mockito.doAnswer($answerExpr) }.asTerm
      case None =>
        // Plain value: wrap in ScalaAnswer.lift
        '{ org.mockito.Mockito.doAnswer(org.mockito.stubbing.ScalaAnswer.lift[Any](_ => $v)) }.asTerm
    }
    doTransformInvocation[S](stubbing, doAnswerCall)
  }

  /** Build a ScalaAnswer that applies a function to extracted InvocationOnMock args with value class support */
  private def buildFunctionAnswer[T: Type](using
      Quotes
  )(
      fn: Expr[T],
      paramTypes: List[quotes.reflect.TypeRepr],
      retType: quotes.reflect.TypeRepr
  ): Expr[ScalaAnswer[Any]] = {
    import quotes.reflect.*

    retType.asType match {
      case '[r] =>
        '{
          org.mockito.stubbing.ScalaAnswer.lift[Any] { invocation =>
            ${
              val fnTerm   = fn.asTerm
              val argExprs = paramTypes.zipWithIndex.map { case (pt, i) =>
                pt.asType match {
                  case '[p] =>
                    val idxExpr = Expr(i)
                    '{ org.mockito.internal.ValueClassWrapper[p].wrapAs[p](invocation.getArgument($idxExpr)) }.asTerm
                }
              }
              val result = Select.unique(fnTerm, "apply").appliedToArgs(argExprs)
              '{ org.mockito.internal.ValueClassExtractor[r].extractAs[r](${ result.asExprOf[r] }) }.asExprOf[Any]
            }
          }
        }
    }
  }

  /**
   * Macro for: Thrown(exception) by mock.method(args)
   */
  inline def thrownBy[T](inline v: Throwable, inline stubbing: T)(using inline $ev: Throwable): T =
    ${ thrownByImpl[T]('v, 'stubbing) }

  // Wrapper for case class usage
  inline def thrownByMacro[T, E](inline v: E, inline stubbing: T): T =
    ${ thrownByMacroImpl[T, E]('v, 'stubbing) }

  private def thrownByMacroImpl[T: Type, E: Type](v: Expr[E], stubbing: Expr[T])(using Quotes): Expr[T] = {
    import quotes.reflect.*

    // Type safety check: E must be a Throwable
    if (!(TypeRepr.of[E] <:< TypeRepr.of[Throwable])) report.errorAndAbort(s"Type mismatch: ${TypeRepr.of[E].show} is not a subtype of Throwable")

    thrownByImpl[T]('{ $v.asInstanceOf[Throwable] }, stubbing)
  }

  def thrownByImpl[T: Type](v: Expr[Throwable], stubbing: Expr[T])(using Quotes): Expr[T] = {
    import quotes.reflect.*

    // Use doAnswer with ScalaThrowsException to avoid Mockito's checked exception validation
    val doThrowCall = '{
      org.mockito.Mockito.doAnswer(new org.mockito.internal.stubbing.answers.ScalaThrowsException($v))
    }.asTerm

    doTransformInvocation[T](stubbing, doThrowCall)
  }

  /**
   * Macro for: Called by mock.method(args)
   */
  inline def calledBy[T](inline stubbing: T): T =
    ${ calledByImpl[T]('stubbing) }

  def calledByImpl[T: Type](stubbing: Expr[T])(using Quotes): Expr[T] = {
    import quotes.reflect.*

    // Use quoted expression to call Mockito.doCallRealMethod()
    val doCallRealMethodCall = '{ org.mockito.Mockito.doCallRealMethod() }.asTerm

    doTransformInvocation[T](stubbing, doCallRealMethodCall)
  }

  /** Build `action.when[ObjType](obj)` — the Stubber.when(T) call shared by all pattern cases */
  private def makeStubberWhenCall(using Quotes)(action: quotes.reflect.Term, obj: quotes.reflect.Term): quotes.reflect.Term = {
    import quotes.reflect.*
    val stubberClass = Symbol.requiredClass("org.mockito.stubbing.Stubber")
    val whenSymbol   = stubberClass.declaredMethod("when").head
    TypeApply(
      Select(action, whenSymbol),
      List(obj.tpe.widen.asType match { case '[t] => TypeTree.of[t] })
    ).appliedTo(obj)
  }

  /**
   * Transform invocation: obj.method(args) => action.when(obj).method(transformedArgs)
   */
  private def transformInvocation(using
      Quotes
  )(
      invocation: quotes.reflect.Term,
      action: quotes.reflect.Term,
      hoisted: mutable.ListBuffer[quotes.reflect.Statement],
      matcherValNames: Set[String] = Set.empty
  ): quotes.reflect.Term = {
    import quotes.reflect.*

    invocation match {
      // Handle blocks with hoisted named args
      case Block(stats, expr) =>
        transformBlock(stats, expr, matcherValNames)((e, mvs) => transformInvocation(e, action, hoisted, mvs))

      // Handle inlined expressions with bindings
      case inlined: Inlined =>
        transformInvocation(inlined.body, action, hoisted, matcherValNames)

      // Match: obj.method(args)
      case Apply(select @ Select(obj, _), args) =>
        val whenCall        = makeStubberWhenCall(action, obj)
        val transformedArgs = transformArgsForApply(select, args, hoisted, matcherValNames)
        Apply(Select(whenCall, select.symbol), transformedArgs)

      // Match: obj.method[TypeArgs](args)
      case Apply(TypeApply(select @ Select(obj, _), targs), args) =>
        val whenCall        = makeStubberWhenCall(action, obj)
        val transformedArgs = transformArgsForApply(TypeApply(select, targs), args, hoisted, matcherValNames)
        Apply(TypeApply(Select(whenCall, select.symbol), targs), transformedArgs)

      // Match: obj.method (no args)
      case select @ Select(obj, _) =>
        Select(makeStubberWhenCall(action, obj), select.symbol)

      // Match: obj.method[TypeArgs] (no args)
      case TypeApply(select @ Select(obj, _), targs) =>
        TypeApply(Select(makeStubberWhenCall(action, obj), select.symbol), targs)

      // Nested Apply - recurse
      case Apply(fun, args) =>
        val transformedArgs = transformArgsForApply(fun, args, hoisted, matcherValNames)
        Apply(
          transformInvocation(fun, action, hoisted, matcherValNames),
          transformedArgs
        )

      case other =>
        report.errorAndAbort(s"Could not transform do-something invocation: ${other.show}")
    }
  }

  private def doTransformInvocation[A: Type](using Quotes)(stubbing: Expr[A], action: quotes.reflect.Term): Expr[A] = {
    import quotes.reflect.*
    val hoisted     = mutable.ListBuffer.empty[Statement]
    val transformed = transformInvocation(stubbing.asTerm, action, hoisted)
    (if (hoisted.nonEmpty) Block(hoisted.toList, transformed) else transformed).asExprOf[A]
  }
}
