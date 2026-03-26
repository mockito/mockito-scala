package org.mockito.internal

import scala.collection.mutable
import scala.quoted.*
import scala.reflect.ClassTag

/**
 * Scala 3 compile-time metadata extractor for mock method handling.
 *
 * At each `mock[T]` call site, this macro inspects `T` and records:
 *   - by-name / vararg parameter indices
 *   - whether a declared return type is value-like (`AnyVal`)
 *   - recoverable concrete return classes for erased `Object` signatures
 *
 * The extracted metadata is stored in [[org.mockito.internal.MockMetadataCache]] and consumed at runtime by `ReflectionUtils` and `ScalaMockHandler`.
 */
object MockMethodMetadata {

  /**
   * Entry point called from Scala 3 `MockCreator` during mock creation.
   *
   * `T` '''must be a concrete type''' at the call site — the macro inspects `T`'s methods at compile time. This is guaranteed when every method in the call chain from user code
   * down to this call is `inline`. If any intermediate method is not `inline`, `T` will be an abstract type variable and the macro will produce no output, leaving the runtime
   * cache empty for that mock type.
   */
  inline def registerByNameAndVarArgInfo[T](using classTag: ClassTag[T]): Unit = ${ registerImpl[T]('classTag) }

  /** Macro implementation that inspects `T` and emits cache-registration runtime code. */
  def registerImpl[T: Type](classTagExpr: Expr[ClassTag[T]])(using Quotes): Expr[Unit] = {
    import quotes.reflect.*

    case class MethodInfo(
        name: String,
        jvmParamTypes: List[Expr[Class[?]]],
        byNameOrVarArgIndices: Set[Int],
        returnsValueClass: Boolean,
        returnTypeClassOpt: Option[Expr[Class[?]]]
    )

    case class ParamInfo(tpe: TypeRepr, index: Int, isByName: Boolean, isVarArg: Boolean)

    def resultTypeOf(tpe: TypeRepr): TypeRepr = tpe match {
      case MethodType(_, _, resultType) => resultTypeOf(resultType)
      case PolyType(_, _, resultType)   => resultTypeOf(resultType)
      case other                        => other
    }

    def isByNameParam(tpe: TypeRepr): Boolean = tpe match {
      case ByNameType(_) => true
      case _             => false
    }

    def isVarArgParam(tpe: TypeRepr): Boolean = tpe match {
      case AnnotatedType(_, annot) if annot.tpe.typeSymbol.fullName == "scala.annotation.internal.Repeated" => true
      case other if other.typeSymbol.fullName == "scala.<repeated>"                                         => true
      case _                                                                                                => false
    }

    def collectParams(tpe: TypeRepr, baseIndex: Int): List[ParamInfo] =
      tpe match {
        case MethodType(_, paramTypes, resultType) =>
          val params = paramTypes.zipWithIndex.map { case (paramType, index) =>
            ParamInfo(paramType, baseIndex + index, isByNameParam(paramType), isVarArgParam(paramType))
          }
          params ++ collectParams(resultType, baseIndex + paramTypes.length)
        case PolyType(_, _, resultType) =>
          collectParams(resultType, baseIndex)
        case _ => Nil
      }

    def declaredOrResolvedReturnType(methodSym: Symbol, methodType: TypeRepr): TypeRepr =
      methodSym.tree match {
        case dd: DefDef => dd.returnTpt.tpe
        case _          => resultTypeOf(methodType)
      }

    def isPrimitiveType(normalized: TypeRepr): Boolean =
      normalized =:= TypeRepr.of[Boolean] ||
        normalized =:= TypeRepr.of[Byte] ||
        normalized =:= TypeRepr.of[Short] ||
        normalized =:= TypeRepr.of[Int] ||
        normalized =:= TypeRepr.of[Long] ||
        normalized =:= TypeRepr.of[Float] ||
        normalized =:= TypeRepr.of[Double] ||
        normalized =:= TypeRepr.of[Char] ||
        normalized =:= TypeRepr.of[Unit]

    def returnsValueClassFor(normalized: TypeRepr): Boolean = {
      val returnSym = normalized.typeSymbol
      returnSym.isClassDef &&
      returnSym != defn.AnyValClass &&
      !isPrimitiveType(normalized) &&
      (normalized <:< TypeRepr.of[AnyVal])
    }

    def returnTypeClassFor(normalized: TypeRepr): Option[Expr[Class[?]]] = {
      val returnSym = normalized.typeSymbol
      if (
        returnSym.isClassDef &&
        !(normalized =:= TypeRepr.of[Any]) &&
        !(normalized =:= TypeRepr.of[AnyRef]) &&
        !(normalized =:= TypeRepr.of[Nothing]) &&
        !(normalized =:= TypeRepr.of[Null])
      ) Some(jvmClassExpr(normalized))
      else None
    }

    def javaVarArgElementType(paramType: TypeRepr): TypeRepr =
      paramType match {
        case AnnotatedType(underlying, _) =>
          underlying match {
            case AppliedType(_, List(elem)) => elem
            case _                          => TypeRepr.of[Object]
          }
        case AppliedType(_, List(elem)) => elem
        case _                          => TypeRepr.of[Object]
      }

    def jvmParamTypeExpr(param: ParamInfo, isJavaMethod: Boolean): Expr[Class[?]] =
      if (param.isByName) '{ classOf[scala.Function0[?]] }
      else if (param.isVarArg) {
        if (isJavaMethod) {
          // Java varargs use array types at JVM level, not Seq.
          val elemClassExpr = jvmClassExpr(javaVarArgElementType(param.tpe))
          '{ java.lang.reflect.Array.newInstance($elemClassExpr, 0).getClass }
        } else '{ classOf[scala.collection.immutable.Seq[?]] }
      } else jvmClassExpr(param.tpe)

    // Phase 1: compile-time analysis of T and its inherited declarations.
    def collectMethodInfos(tpe: TypeRepr, typeSymbol: Symbol): List[MethodInfo] = {
      val allMethods = {
        // Deduplicate by symbol identity: the same symbol may appear in both `declarations`
        // and `baseClasses.flatMap(_.declarations)`, so we use the symbol itself as the key.
        // Using `fullName` would wrongly deduplicate overloaded methods (e.g. `request()`,
        // `request(String...)` and `request(MediaType...)` all share the same fullName).
        val seen = mutable.Set.empty[Symbol]
        (typeSymbol.declarations ++ tpe.baseClasses.flatMap(_.declarations))
          .filter(symbol => symbol.isDefDef && !symbol.isClassConstructor && seen.add(symbol))
      }

      allMethods.flatMap { methodSym =>
        val methodType           = tpe.memberType(methodSym)
        val isJavaMethod         = methodSym.flags.is(Flags.JavaDefined)
        val params               = collectParams(methodType, 0)
        val byNameOrVarArgFields = params.collect { case param if param.isByName || param.isVarArg => param.index }.toSet
        val inferredReturnType   = resultTypeOf(methodType).dealias.simplified
        val normalizedReturnType = declaredOrResolvedReturnType(methodSym, methodType).dealias.simplified
        val returnsValueClass    = returnsValueClassFor(normalizedReturnType)
        val returnTypeClassOpt   =
          if (!(normalizedReturnType =:= inferredReturnType)) returnTypeClassFor(normalizedReturnType)
          else None
        val jvmParamTypes = params.map(param => jvmParamTypeExpr(param, isJavaMethod))
        if (byNameOrVarArgFields.nonEmpty || returnsValueClass || returnTypeClassOpt.nonEmpty)
          Some(
            MethodInfo(
              methodSym.name,
              jvmParamTypes,
              byNameOrVarArgFields,
              returnsValueClass,
              returnTypeClassOpt
            )
          )
        else None
      }
    }

    // Phase 2: generate serialized registration payload that can be consumed at runtime.
    def buildRegistrationsExpr(methodInfos: List[MethodInfo]): Expr[List[(String, List[Class[?]], Set[Int], Boolean, Option[Class[?]])]] = {
      val registrations = methodInfos.map { info =>
        val nameExpr              = Expr(info.name)
        val paramTypesExpr        = Expr.ofList(info.jvmParamTypes)
        val indicesExpr           = Expr(info.byNameOrVarArgIndices)
        val returnsValueClassExpr = Expr(info.returnsValueClass)
        val returnTypeClassExpr   = info.returnTypeClassOpt match {
          case Some(returnTypeClass) => '{ Some($returnTypeClass) }
          case None                  => '{ None }
        }
        '{ ($nameExpr, $paramTypesExpr, $indicesExpr, $returnsValueClassExpr, $returnTypeClassExpr) }
      }
      Expr.ofList(registrations)
    }

    // Phase 3: runtime method lookup and cache registration.
    def emitRuntimeRegistration(
        classTagExpr: Expr[ClassTag[T]],
        registrationsExpr: Expr[List[(String, List[Class[?]], Set[Int], Boolean, Option[Class[?]])]]
    ): Expr[Unit] = {
      val classExpr = '{ $classTagExpr.runtimeClass }
      '{
        val clazz = $classExpr
        if (clazz != classOf[Any]) {
          val infos               = $registrationsExpr
          val resolvedMethodInfos = infos.flatMap { case (name, paramTypes, indices, returnsValueClass, returnTypeClassOpt) =>
            try {
              val method = clazz.getMethod(name, paramTypes*)
              Some((method, indices, returnsValueClass, returnTypeClassOpt))
            } catch {
              case _: NoSuchMethodException => None
            }
          }

          val byNameInfos =
            resolvedMethodInfos.collect { case (method, indices, _, _) if indices.nonEmpty => (method, indices) }
          if (byNameInfos.nonEmpty) MockMetadataCache.registerByName(clazz, byNameInfos)

          val returnsValueClassInfos = resolvedMethodInfos.collect { case (method, _, true, _) => (method, true) }
          if (returnsValueClassInfos.nonEmpty) MockMetadataCache.registerReturnsValueClass(returnsValueClassInfos)

          val returnTypeInfos = resolvedMethodInfos.collect { case (method, _, _, Some(returnTypeClass)) =>
            (method, returnTypeClass)
          }
          if (returnTypeInfos.nonEmpty) MockMetadataCache.registerReturnType(returnTypeInfos)
        }
      }
    }

    val tpe         = TypeRepr.of[T].dealias
    val methodInfos = collectMethodInfos(tpe, tpe.typeSymbol)

    if (methodInfos.isEmpty) '{ () } else emitRuntimeRegistration(classTagExpr, buildRegistrationsExpr(methodInfos))
  }

  /**
   * Convert a Scala `TypeRepr` to a JVM `Class` expression.
   *
   * Primitive types map to `TYPE` singletons; reference types resolve through `Class.forName`.
   */
  private def jvmClassExpr(using Quotes)(tpe: quotes.reflect.TypeRepr): Expr[Class[?]] = {
    import quotes.reflect.*
    tpe.dealias.simplified.widen match {
      case t if t =:= TypeRepr.of[Boolean] => '{ java.lang.Boolean.TYPE }
      case t if t =:= TypeRepr.of[Byte]    => '{ java.lang.Byte.TYPE }
      case t if t =:= TypeRepr.of[Short]   => '{ java.lang.Short.TYPE }
      case t if t =:= TypeRepr.of[Int]     => '{ java.lang.Integer.TYPE }
      case t if t =:= TypeRepr.of[Long]    => '{ java.lang.Long.TYPE }
      case t if t =:= TypeRepr.of[Float]   => '{ java.lang.Float.TYPE }
      case t if t =:= TypeRepr.of[Double]  => '{ java.lang.Double.TYPE }
      case t if t =:= TypeRepr.of[Char]    => '{ java.lang.Character.TYPE }
      case t if t =:= TypeRepr.of[Unit]    => '{ java.lang.Void.TYPE }
      case t                               =>
        val name     = t.typeSymbol.fullName
        val nameExpr = Expr(name)
        '{
          try Class.forName($nameExpr)
          catch { case _: Exception => classOf[Object] }
        }
    }
  }

  /**
   * Extract runtime classes from an intersection/refined type (`Foo & Bar`).
   *
   * Returns only extra interfaces (primary runtime class excluded). Example: `mock[Foo & Bar]` yields `List(classOf[Bar])`.
   */
  inline def extraInterfacesImpl[T](using classTag: ClassTag[T]): List[Class[?]] = ${ extractExtraInterfaces[T]('classTag) }

  /** Macro backend for [[extraInterfacesImpl]]. */
  private def extractExtraInterfaces[T: Type](classTagExpr: Expr[ClassTag[T]])(using Quotes): Expr[List[Class[?]]] = {
    import quotes.reflect.*

    def collectTypes(tpe: TypeRepr): List[TypeRepr] = tpe.dealias match {
      case AndType(left, right) => collectTypes(left) ++ collectTypes(right)
      case other                => List(other)
    }

    val allTypes   = collectTypes(TypeRepr.of[T])
    val classExprs = allTypes.map { t =>
      val name     = t.typeSymbol.fullName
      val nameExpr = Expr(name)
      '{
        try Some(Class.forName($nameExpr))
        catch { case _: Exception => None }
      }
    }
    val listExpr = Expr.ofList(classExprs)

    '{
      val primary = $classTagExpr.runtimeClass
      $listExpr.flatMap(_.toList).filterNot(_ == primary)
    }
  }
}
