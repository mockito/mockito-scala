package org.mockito

import org.mockito.Utils.*
import org.mockito.WhenDslKeywords.*
import org.mockito.WhenMacroRuntime.{ AnswerActions, AnswerPFActions, RealMethod }
import org.mockito.stubbing.{ OngoingStubbing, ScalaFirstStubbing, ScalaOngoingStubbing }
import org.scalactic.Prettifier

import scala.collection.mutable
import scala.quoted.*

/**
 * Scala 3 macro implementations for the idiomatic when/stubbing DSL.
 *
 * High-level flow:
 *   1. Parse the stubbed invocation syntax tree.
 *   2. Rewrite args so raw literals are wrapped consistently while matcher args are preserved.
 *   3. Emit regular Mockito runtime calls (`Mockito.when`, stubbing wrappers), so runtime behavior stays in shared code.
 *
 * Runtime support classes are in [[WhenMacroRuntime]].
 */
object WhenMacro {
  // Re-export runtime classes for backward compatibility
  type AnswerActions[T]   = WhenMacroRuntime.AnswerActions[T]
  type AnswerPFActions[T] = WhenMacroRuntime.AnswerPFActions[T]
  val RealMethod = WhenMacroRuntime.RealMethod

  // Raw macro: transforms arguments and wraps in Mockito.when, returns OngoingStubbing[T]
  // Used by cats/scalaz modules to build their own action wrappers
  inline def whenRaw[T](inline stubbing: T): OngoingStubbing[T] =
    ${ whenRawMacro[T]('stubbing) }

  def whenRawMacro[T: Type](stubbing: Expr[T])(using Quotes): Expr[OngoingStubbing[T]] = {
    val transformed = doTransformInvocation(stubbing)
    '{ org.mockito.Mockito.when[T]($transformed) }
  }

  // Macro methods for IdiomaticStubbing extension methods (return action objects)
  inline def shouldReturn[T](inline stubbing: T): Any =
    ${ shouldReturnMacro[T]('stubbing) }

  def shouldReturnMacro[T: Type](stubbing: Expr[T])(using Quotes): Expr[Any] = {
    import quotes.reflect.*
    buildActionWrapper[T]("org.mockito.IdiomaticMockitoBaseRuntime.ReturnActions", buildScalaFirstStubbing[T](stubbing))
  }

  inline def shouldThrow[T](inline stubbing: T): Any =
    ${ shouldThrowMacro[T]('stubbing) }

  def shouldThrowMacro[T: Type](stubbing: Expr[T])(using Quotes): Expr[Any] = {
    import quotes.reflect.*
    buildActionWrapper[T]("org.mockito.IdiomaticMockitoBaseRuntime.ThrowActions", buildScalaFirstStubbing[T](stubbing))
  }

  inline def shouldAnswer[T](inline stubbing: T): Any =
    ${ shouldAnswerMacro[T]('stubbing) }

  def shouldAnswerMacro[T: Type](stubbing: Expr[T])(using Quotes): Expr[Any] = {
    import quotes.reflect.*
    buildActionWrapper[T]("org.mockito.WhenMacroRuntime.AnswerActions", buildScalaFirstStubbing[T](stubbing))
  }

  inline def shouldAnswerPF[T](inline stubbing: T): Any =
    ${ shouldAnswerPFMacro[T]('stubbing) }

  def shouldAnswerPFMacro[T: Type](stubbing: Expr[T])(using Quotes): Expr[Any] = {
    import quotes.reflect.*
    buildActionWrapper[T]("org.mockito.WhenMacroRuntime.AnswerPFActions", buildScalaFirstStubbing[T](stubbing))
  }

  inline def shouldReturn[T, R](inline stubbing: => T, inline v: => R)(using $pt: Prettifier): OngoingStubbing[T] =
    ${ shouldReturnImpl[T, R]('stubbing, 'v, '$pt) }

  def shouldReturnImpl[T: Type, R: Type](
      stubbing: Expr[T],
      v: Expr[R],
      pt: Expr[Prettifier]
  )(using Quotes): Expr[OngoingStubbing[T]] = {
    import quotes.reflect.*
    val returnActionsClass = Symbol.requiredClass("org.mockito.IdiomaticMockitoBaseRuntime.ReturnActions")
    Apply(
      Select.overloaded(New(TypeTree.ref(returnActionsClass)), "<init>", List(TypeRepr.of[T]), Nil),
      List(buildScalaFirstStubbing[T](stubbing))
    ).asExprOf[OngoingStubbing[T]]
  }

  inline def isLenient[T](inline stubbing: => T)(using $pt: Prettifier): ScalaFirstStubbing[T] =
    ${ isLenientImpl[T]('stubbing, '$pt) }

  def isLenientImpl[T: Type](
      stubbing: Expr[T],
      pt: Expr[Prettifier]
  )(using Quotes): Expr[ScalaFirstStubbing[T]] = {
    val transformed = doTransformInvocation(stubbing)
    '{
      val s = new ScalaFirstStubbing[T](org.mockito.Mockito.when[T]($transformed))
      s.isLenient()
      s
    }
  }

  inline def shouldCallRealMethod[T](inline stubbing: => T)(using $pt: Prettifier): ScalaOngoingStubbing[T] =
    ${ shouldCallRealMethodImpl[T]('stubbing, '$pt) }

  def shouldCallRealMethodImpl[T: Type](
      stubbing: Expr[T],
      pt: Expr[Prettifier]
  )(using Quotes): Expr[ScalaOngoingStubbing[T]] = {
    val transformed = doTransformInvocation(stubbing)
    '{ new ScalaOngoingStubbing[T](org.mockito.Mockito.when[T]($transformed).thenCallRealMethod()) }
  }

  inline def shouldThrow[T](inline stubbing: => T, inline throwables: Throwable*)(using $pt: Prettifier): OngoingStubbing[T] =
    ${ shouldThrowImpl[T]('stubbing, 'throwables, '$pt) }

  def shouldThrowImpl[T: Type](
      stubbing: Expr[T],
      throwables: Expr[Seq[Throwable]],
      pt: Expr[Prettifier]
  )(using Quotes): Expr[OngoingStubbing[T]] = {
    import quotes.reflect.*
    val throwActionsClass = Symbol.requiredClass("org.mockito.IdiomaticMockitoBaseRuntime.ThrowActions")
    Apply(
      Select.overloaded(New(TypeTree.ref(throwActionsClass)), "<init>", List(TypeRepr.of[T]), Nil),
      List(buildScalaFirstStubbing[T](stubbing))
    ).asExprOf[OngoingStubbing[T]]
  }

  inline def shouldAnswer[T, P1, R](inline stubbing: => T, inline f: Any)(using $pt: Prettifier): AnswerActions[T] =
    ${ shouldAnswerImpl[T, P1, R]('stubbing, 'f, '$pt) }

  def shouldAnswerImpl[T: Type, P1: Type, R: Type](
      stubbing: Expr[T],
      f: Expr[Any],
      pt: Expr[Prettifier]
  )(using Quotes): Expr[AnswerActions[T]] = {
    val transformed = doTransformInvocation(stubbing)
    '{ new WhenMacroRuntime.AnswerActions[T](org.mockito.Mockito.when[T]($transformed)) }
  }

  inline def shouldAnswerPF[T, P, R](inline stubbing: => T, inline f: PartialFunction[P, R])(using $pt: Prettifier): AnswerPFActions[T] =
    ${ shouldAnswerPFImpl[T, P, R]('stubbing, 'f, '$pt) }

  def shouldAnswerPFImpl[T: Type, P: Type, R: Type](
      stubbing: Expr[T],
      f: Expr[PartialFunction[P, R]],
      pt: Expr[Prettifier]
  )(using Quotes): Expr[AnswerPFActions[T]] = {
    val transformed = doTransformInvocation(stubbing)
    '{ new WhenMacroRuntime.AnswerPFActions[T](org.mockito.Mockito.when[T]($transformed)) }
  }

  /**
   * Transform a method invocation by wrapping non-matcher arguments in DefaultMatcher
   */
  private def transformInvocation(using
      Quotes
  )(
      invocation: quotes.reflect.Term,
      hoisted: mutable.ListBuffer[quotes.reflect.Statement],
      matcherValNames: Set[String] = Set.empty
  ): quotes.reflect.Term = {
    import quotes.reflect.*

    invocation match {
      // Handle inlined expressions (from Scala 3 inline expansion)
      case Inlined(call, bindings, expansion) =>
        Inlined(call, bindings, transformInvocation(expansion, hoisted, matcherValNames))

      // Handle blocks with hoisted named args (Scala 3 desugars named args into val defs)
      case Block(stats, expr) =>
        transformBlock(stats, expr, matcherValNames)(transformInvocation(_, hoisted, _))

      // Match: obj.method[TypeArgs](args1)(args2)...
      case Apply(fun, args) =>
        val transformedArgs = transformArgsForApply(fun, args, hoisted, matcherValNames)
        Apply(transformInvocation(fun, hoisted, matcherValNames), transformedArgs)

      // Match: obj.method[TypeArgs]
      case TypeApply(fun, targs) =>
        TypeApply(transformInvocation(fun, hoisted, matcherValNames), targs)

      // Match: obj.method (no args, no type args) or base case
      case _ => invocation
    }
  }

  private def doTransformInvocation[T: Type](using Quotes)(stubbing: Expr[T]): Expr[T] = {
    import quotes.reflect.*
    val hoisted     = mutable.ListBuffer.empty[Statement]
    val transformed = transformInvocation(stubbing.asTerm, hoisted)
    (if (hoisted.nonEmpty) Block(hoisted.toList, transformed) else transformed).asExprOf[T]
  }

  /**
   * Build: new ScalaFirstStubbing[T](Mockito.when[T](transformed)) Shared by shouldReturnMacro/shouldThrowMacro/shouldAnswerMacro/shouldAnswerPFMacro.
   */
  private def buildScalaFirstStubbing[T: Type](using Quotes)(stubbing: Expr[T]): quotes.reflect.Term = {
    import quotes.reflect.*
    val transformed             = doTransformInvocation(stubbing).asTerm
    val scalaFirstStubbingClass = Symbol.requiredClass("org.mockito.stubbing.ScalaFirstStubbing")
    val mockitoClass            = Symbol.requiredModule("org.mockito.Mockito")
    val whenCall                = TypeApply(Select.unique(Ref(mockitoClass), "when"), List(TypeTree.of[T])).appliedTo(transformed)
    TypeApply(
      Select(New(TypeTree.ref(scalaFirstStubbingClass)), scalaFirstStubbingClass.primaryConstructor),
      List(TypeTree.of[T])
    ).appliedTo(whenCall)
  }

  /**
   * Build: new ActionClass[T](scalaStubbing).asExprOf[Any] actionClassName is a fully-qualified class name, e.g. "org.mockito.IdiomaticMockitoBaseRuntime.ReturnActions"
   */
  private def buildActionWrapper[T: Type](using Quotes)(actionClassName: String, scalaStubbing: quotes.reflect.Term): Expr[Any] = {
    import quotes.reflect.*
    val actionClass = Symbol.requiredClass(actionClassName)
    TypeApply(
      Select(New(TypeTree.ref(actionClass)), actionClass.primaryConstructor),
      List(TypeTree.of[T])
    ).appliedTo(scalaStubbing).asExprOf[Any]
  }
}
