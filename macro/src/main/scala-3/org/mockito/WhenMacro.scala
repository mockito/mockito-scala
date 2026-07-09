package org.mockito

import org.mockito.Utils.*
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

  /**
   * Value-aware stubbing used by `shouldReturn`/`mustReturn`/`returns`.
   *
   * Taking the returned value(s) as a separate type parameter `V` (rather than unifying with the stubbed type `T`) lets this macro decide, by inspecting the two types, how to
   * build the stubbing:
   *   - `V` conforms to `T` (normal/sub-typed value): stub at `T`.
   *   - `V` numerically widens to `T` (e.g. `Long` method stubbed with an `Int`): stub at `T`, coercing the value with `toLong`/`toDouble`/... so the runtime value has the
   *     method's return type instead of being boxed as the narrower type.
   *   - `T` is a polymorphic type whose parameter defaulted (e.g. `Either[String, Resp[Any]]`) while `V` is more specific: stub at `V`, so the value's type drives inference.
   *   - otherwise the stub is a genuine type error and is rejected with a clear message.
   */
  transparent inline def returnsValue[T, V](inline stubbing: T, inline value: V, inline values: V*): ScalaOngoingStubbing[?] =
    ${ returnsValueMacro[T, V]('stubbing, 'value, 'values) }

  // Scala weak conformance is not a linear order: the main chain is Byte < Short < Int < Long < Float <
  // Double, while Char widens directly to Int (and wider) but is unrelated to Byte/Short. Each key maps
  // to the set of types it may widen to, so e.g. Byte -> Char is correctly *not* a widening.
  private val numericWidening: Map[String, Set[String]] = {
    val chain   = List("scala.Byte", "scala.Short", "scala.Int", "scala.Long", "scala.Float", "scala.Double")
    val onChain = chain.tails.collect { case from :: wider => from -> wider.toSet }.toMap
    onChain + ("scala.Char" -> Set("scala.Int", "scala.Long", "scala.Float", "scala.Double"))
  }

  def returnsValueMacro[T: Type, V: Type](stubbing: Expr[T], value: Expr[V], values: Expr[Seq[V]])(using Quotes): Expr[ScalaOngoingStubbing[?]] = {
    import quotes.reflect.*
    val transformed = doTransformInvocation(stubbing)
    val tT          = TypeRepr.of[T].widen.dealias
    val tV          = TypeRepr.of[V].widen.dealias

    val numericWiden = numericWidening.get(tV.typeSymbol.fullName).exists(_.contains(tT.typeSymbol.fullName))

    // Numeric literal narrowing (per the spec, mirrored by plain Scala assignment and the Scala 2 DSL):
    // an `Int` *constant* whose value is in the target's range may narrow to Byte/Short/Char. `Expr#value`
    // returns `Some` exactly for compile-time constants (literals and constant-folded `final val`s), so
    // out-of-range literals and non-constant `Int` values are still rejected, just like the compiler.
    def intConst(e: Expr[V]): Option[Int] = if (tV =:= TypeRepr.of[Int]) e.asExprOf[Int].value else None
    def inTargetRange(n: Int): Boolean    = tT.typeSymbol.fullName match {
      case "scala.Byte"  => n >= Byte.MinValue && n <= Byte.MaxValue
      case "scala.Short" => n >= Short.MinValue && n <= Short.MaxValue
      case "scala.Char"  => n >= Char.MinValue && n <= Char.MaxValue
      case _             => false
    }
    val literalNarrow =
      intConst(value).exists(inTargetRange) && Varargs.unapply(values).exists(_.forall(intConst(_).exists(inTargetRange)))

    // Treats `Any`/`Nothing` positions in the target as wildcards, so a polymorphic method whose
    // type parameter defaulted to `Any` accepts a more specific value, while genuinely unrelated
    // types (e.g. `Int` vs `String`) are rejected.
    def compatible(t: TypeRepr, v: TypeRepr): Boolean =
      (v <:< t) || (t =:= TypeRepr.of[Any]) || (t =:= TypeRepr.of[Nothing]) || {
        (t, v) match {
          case (AppliedType(c1, a1), AppliedType(c2, a2)) if c1 =:= c2 && a1.length == a2.length =>
            a1.zip(a2).forall((x, y) => compatible(x, y))
          case _ => false
        }
      }

    // A single quote is emitted per expansion (rather than unifying several `if`/`else` branches),
    // so `transparent inline` can refine the result to the precise `ScalaOngoingStubbing[targetType]`.
    val boxed: Map[String, TypeRepr] = Map(
      "scala.Byte"    -> TypeRepr.of[java.lang.Byte],
      "scala.Short"   -> TypeRepr.of[java.lang.Short],
      "scala.Char"    -> TypeRepr.of[java.lang.Character],
      "scala.Int"     -> TypeRepr.of[java.lang.Integer],
      "scala.Long"    -> TypeRepr.of[java.lang.Long],
      "scala.Float"   -> TypeRepr.of[java.lang.Float],
      "scala.Double"  -> TypeRepr.of[java.lang.Double],
      "scala.Boolean" -> TypeRepr.of[java.lang.Boolean]
    )
    // `Int` stubbed on a method returning e.g. `java.lang.Long` (typically Java interop): a Scala numeric
    // that weakly conforms to the target's primitive is widened and boxed, matching the Scala 2 DSL.
    // `boxWidenPrim` is the primitive to widen the value to before boxing (the value's own type when its
    // box already conforms, e.g. for a supertype target like `Number`).
    val boxWidenPrim: Option[String] = {
      val widenTargets = tV.typeSymbol.fullName :: numericWidening.getOrElse(tV.typeSymbol.fullName, Set.empty).toList
      widenTargets.find(p => boxed.get(p).exists(_ <:< tT))
    }
    val boxWiden = boxWidenPrim.isDefined

    val numericCoerce: Option[String] = Option.when(numericWiden || literalNarrow)("to" + tT.typeSymbol.name)
    // `Any`/`Nothing` (e.g. `returns ???`): the value can't be statically re-typed, so cast at runtime.
    val runtimeCast                                     = tV =:= TypeRepr.of[Any]
    val (targetType, castStubbing): (TypeRepr, Boolean) =
      if (tV <:< tT || numericWiden || literalNarrow || boxWiden || runtimeCast) (tT, false)
      else if (compatible(tT, tV)) (tV, true)
      else report.errorAndAbort(s"Cannot stub a method returning ${tT.show} with a value of type ${tV.show}")

    targetType.asType match {
      case '[tgt] =>
        val whenArg: Expr[tgt]            = if (castStubbing) '{ $transformed.asInstanceOf[tgt] } else transformed.asExprOf[tgt]
        def coerce(e: Expr[V]): Expr[tgt] =
          numericCoerce match {
            case Some(n)       => Select.unique(e.asTerm, n).asExprOf[tgt]
            case _ if boxWiden =>
              // Widen to the target's primitive (a no-op when it equals the value's type), then box via `Any`.
              val prim               = boxWidenPrim.get
              val widened: Expr[Any] =
                if (prim == tV.typeSymbol.fullName) e.asExprOf[Any]
                else Select.unique(e.asTerm, "to" + prim.stripPrefix("scala.")).asExprOf[Any]
              '{ $widened.asInstanceOf[tgt] }
            case _ if runtimeCast => '{ $e.asInstanceOf[tgt] }
            case _                => e.asExprOf[tgt]
          }
        val plainPassThrough     = numericCoerce.isEmpty && !boxWiden && !runtimeCast
        val first: Expr[tgt]     = coerce(value)
        val rest: Expr[Seq[tgt]] =
          if (plainPassThrough) values.asExprOf[Seq[tgt]]
          else '{ $values.map(x => ${ coerce('x) }) }
        '{ new ScalaFirstStubbing[tgt](org.mockito.Mockito.when[tgt]($whenArg)).thenReturn($first, $rest*) }
    }
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
