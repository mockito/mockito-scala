package org.mockito

import org.mockito.MacroConstants.{ isSpecs2Matcher, MockitoMatchers }
import scala.collection.mutable
import scala.quoted.*

/** Shared Scala 3 macro tree utilities used by macro implementation types. */
object Utils {

  /** Find the type of the single constructor parameter of a value class, or abort with a macro error */
  private[mockito] def findValueClassParamType(using Quotes)(tpe: quotes.reflect.TypeRepr): quotes.reflect.TypeRepr = {
    import quotes.reflect.*
    tpe.typeSymbol.primaryConstructor.paramSymss.flatten
      .collectFirst { case p if p.isTerm => tpe.memberType(p).widen }
      .getOrElse(report.errorAndAbort(s"Could not find constructor parameter for value class ${tpe.show}"))
  }

  /** Extract (paramTypes, retType) when `tpe` is a FunctionN type, or None for non-function types */
  private[mockito] def extractFunctionInfo(using Quotes)(tpe: quotes.reflect.TypeRepr): Option[(List[quotes.reflect.TypeRepr], quotes.reflect.TypeRepr)] = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(tycon, args) if args.nonEmpty && tycon.typeSymbol.fullName.startsWith("scala.Function") =>
        Some((args.init, args.last))
      case _ => None
    }
  }

  /** Extract type argument trees from an applied type, or Nil for non-generic types */
  private[mockito] def typeArgTrees(using Quotes)(tpe: quotes.reflect.TypeRepr): List[quotes.reflect.TypeTree] = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(_, args) => args.map(a => TypeTree.of(using a.asType))
      case _                    => Nil
    }
  }

  /** Encode operator symbols to their Scala name-mangled equivalents */
  private def encodeOperatorName(name: String): String = name match {
    case "*"  => "$times"
    case ">"  => "$greater"
    case ">=" => "$greater$eq"
    case "<"  => "$less"
    case "<=" => "$less$eq"
    case "=~" => "$eq$tilde"
    case _    => name
  }

  /** Check if a list of expressions contains any matchers */
  private[mockito] def hasMatchers(using Quotes)(args: List[quotes.reflect.Term]): Boolean =
    args.exists(arg => isMatcher(arg))

  /** Check if an expression is a Mockito matcher */
  private[mockito] def isMatcher(using Quotes)(arg: quotes.reflect.Term): Boolean = {
    import quotes.reflect.*

    // Extract method name from the tree by recursively traversing it
    def extractMethodName(tree: Term): Option[String] = tree match {

      // Match: obj.method[TypeArgs](args) - the most common matcher pattern
      case Apply(TypeApply(Select(qual, methodName), _), _) =>
        Some(methodName.toString)

      // Match: obj.method(args)
      case Apply(Select(qual, methodName), _) =>
        // Check if this is Captor.asCapture[T].apply(x)
        qual match {
          case TypeApply(Select(captorQual, "asCapture"), _) if captorQual.show.contains("Captor") && methodName == "apply" =>
            Some("Captor.asCapture")
          // Scala 3 given Conversion: asCapture is accessed via Ident, not Select
          case TypeApply(Ident("asCapture"), _) if methodName == "apply" =>
            Some("Captor.asCapture")
          case _ =>
            Some(methodName.toString)
        }

      // Match: obj.method[TypeArgs]
      case TypeApply(Select(qual, methodName), _) =>
        Some(methodName.toString)

      // Match: obj.method
      case Select(qual, methodName) =>
        Some(methodName.toString)

      // Match: imported method name (e.g. `argThat` from `import ArgumentMatchersSugar.*`)
      case Ident(name) =>
        Some(name.toString)

      // Recurse into Apply nodes to handle nested qualifiers
      // This handles cases like: obj.nested.method[T](args)
      case Apply(fun, _) =>
        extractMethodName(fun)

      // Recurse into TypeApply nodes
      case TypeApply(fun, _) =>
        extractMethodName(fun)

      case _ =>
        None
    }

    // Unwrap Inlined/Block wrappers from Scala 3 inline expansion before checking
    arg match {
      case Inlined(_, _, expansion) => isMatcher(expansion)
      case Block(_, expr)           => isMatcher(expr)
      case _                        =>
        // Check if the tree representation contains MacroMatchers
        if (arg.show.contains("org.mockito.matchers.MacroMatchers")) true
        else {
          val methodName = extractMethodName(arg).map(encodeOperatorName)
          methodName.exists(mn => MockitoMatchers.contains(mn) || isSpecs2Matcher(mn))
        }
    }
  }

  /** Transform a list of argument trees, wrapping non-matchers in DefaultMatcher */
  private[mockito] def transformArgs(using Quotes)(args: List[quotes.reflect.Term], matcherValNames: Set[String] = Set.empty): List[quotes.reflect.Term] =
    args.map(arg => transformArg(arg, matcherValNames))

  /**
   * Transform args for an Apply node, hoisting DefaultMatcher calls for by-name parameters. Returns (hoisted val definitions, transformed args). By-name params need hoisting
   * because the compiler wraps them in thunks AFTER macro expansion, which would delay DefaultMatcher registration and cause "Invalid use of matchers" errors.
   */
  private[mockito] def transformArgsForApply(using
      Quotes
  )(
      fun: quotes.reflect.Term,
      args: List[quotes.reflect.Term],
      hoistedBindings: mutable.ListBuffer[quotes.reflect.Statement],
      matcherValNames: Set[String] = Set.empty
  ): List[quotes.reflect.Term] = {
    import quotes.reflect.*

    // Get parameter info (name, type) from the method type
    val paramInfo: List[(String, TypeRepr)] = fun.tpe.widen match {
      case mt @ MethodType(paramNames, paramTypes, _) =>
        paramNames.zip(paramTypes.map {
          case ByNameType(inner) => inner
          case other             => other
        })
      case _ => Nil
    }

    // Transform all args normally first
    val transformed = transformArgs(args, matcherValNames)

    // Fix matchers with type Nothing — in Scala 3, `*` infers T=Nothing which returns null.
    // For primitive params, null causes NPE during unboxing. Fix by adding .asInstanceOf[ExpectedType].
    if (paramInfo.isEmpty) transformed
    else
      transformed.zipWithIndex.map { case (arg, idx) =>
        val (innerArg, argName) = arg match {
          case NamedArg(name, value) => (value, Some(name))
          case other                 => (other, None)
        }

        // Resolve expected type by name (for named args) or by position
        val expectedType: Option[TypeRepr] = argName match {
          case Some(name) =>
            paramInfo.collectFirst { case (pName, pType) if pName == name => pType }
          case None if idx < paramInfo.length =>
            Some(paramInfo(idx)._2)
          case _ => None
        }

        // Fix args with type Nothing — in Scala 3, `*` infers T=Nothing which returns null.
        // Scala 3 may hoist `*` into a val (for named args), so check arg type regardless of matcher detection.
        // For primitive params, null causes NPE during unboxing. Fix by adding .asInstanceOf[ExpectedType].
        val innerTypeIsNothing = innerArg.tpe.widen =:= TypeRepr.of[Nothing]
        if (innerTypeIsNothing)
          expectedType match {
            case Some(eType) if !(eType =:= TypeRepr.of[Nothing]) =>
              eType.asType match {
                case '[t] =>
                  val fixedInner = '{ ${ innerArg.asExprOf[Any] }.asInstanceOf[t] }.asTerm
                  argName match {
                    case Some(name) => NamedArg(name, fixedInner)
                    case None       => fixedInner
                  }
              }
            case _ => arg
          }
        else arg
      }
  }

  /**
   * Transform a single argument tree, wrapping non-matchers in DefaultMatcher. matcherValNames: set of val names known to hold matcher calls (from hoisted named args in Blocks).
   */
  private[mockito] def transformArg(using Quotes)(arg: quotes.reflect.Term, matcherValNames: Set[String] = Set.empty): quotes.reflect.Term = {
    import quotes.reflect.*

    val isArgMatcher = isMatcher(arg) || isMatcherValRef(arg, matcherValNames)

    if (isArgMatcher) {
      arg
    } else {
      arg match {
        // Handle Inlined wrappers (from Scala 3 inline expansion)
        case Inlined(call, bindings, expansion) =>
          Inlined(call, bindings, transformArg(expansion, matcherValNames))

        // Handle Block wrappers
        case Block(stats, expr) =>
          Block(stats, transformArg(expr, matcherValNames))

        // Skip synthetic variables (generated by compiler)
        case _ if arg.show.startsWith("x$") => arg

        // Match named arguments: name = value
        case NamedArg(name, value) =>
          // Recursively transform the value and preserve the name
          NamedArg(name, transformArg(value, matcherValNames))

        // Match explicit varargs: arg: _* (Typed with * in source code)
        case Typed(expr, tpt) if arg.show.contains(": _*") =>
          // Wrap in DefaultMatcher and preserve varargs typing
          val defaultMatcher = Symbol.requiredModule("org.mockito.matchers.DefaultMatcher")
          val applyMethods   = defaultMatcher.methodMembers.filter(_.name == "apply")
          val applyMethod    = applyMethods.headOption.getOrElse(
            report.errorAndAbort(s"Could not find apply method in DefaultMatcher")
          )

          // Get the type of the expr (the Seq itself) to provide as type parameter
          val exprType = expr.tpe.widen.asType

          Typed(
            Apply(
              TypeApply(
                Select(Ref(defaultMatcher), applyMethod),
                List(TypeTree.of(using exprType))
              ),
              List(expr)
            ),
            tpt
          )

        // Match vararg values: Typed(Repeated(values, _), _*)
        // This is the case for method(1, 2, 3) where method has signature (args: Int*)
        // Always transform each element individually (wrap non-matchers in DefaultMatcher)
        // so that matcher count matches arg count after ScalaMockHandler unwraps the Seq.
        case Typed(repeated @ Repeated(elems, elemTpt), tpt) =>
          val transformedElems = elems.map(elem => transformArg(elem, matcherValNames))
          Typed(Repeated(transformedElems, elemTpt), tpt)

        // Regular Typed - unwrap and transform
        case Typed(expr, tpt) =>
          // Unwrap the typed node and just transform the expression
          transformArg(expr, matcherValNames)

        // Any other argument - wrap in DefaultMatcher
        case _ =>
          // Get the apply method symbol explicitly
          val defaultMatcher = Symbol.requiredModule("org.mockito.matchers.DefaultMatcher")
          val applyMethods   = defaultMatcher.methodMembers.filter(_.name == "apply")

          val applyMethod = applyMethods.headOption.getOrElse(
            report.errorAndAbort(s"Could not find apply method in DefaultMatcher")
          )

          // Get the type of the argument to provide as type parameter
          val argType = arg.tpe.widen.asType

          // Build: DefaultMatcher.apply[T](arg) where T is the argument type
          Apply(
            TypeApply(
              Select(Ref(defaultMatcher), applyMethod),
              List(TypeTree.of(using argType))
            ),
            List(arg)
          )
      }
    }
  }

  /** Check if an arg is an Ident referencing a known matcher val (hoisted from named arg) */
  private def isMatcherValRef(using Quotes)(arg: quotes.reflect.Term, matcherValNames: Set[String]): Boolean = {
    import quotes.reflect.*
    if (matcherValNames.isEmpty) false
    else
      arg match {
        case NamedArg(_, value) => isMatcherValRef(value, matcherValNames)
        case Ident(name)        => matcherValNames.contains(name)
        case _                  => false
      }
  }

  /** Detect val defs in Block stats whose rhs is a matcher call (hoisted from named args by Scala 3 compiler) */
  private[mockito] def detectMatcherValDefs(using Quotes)(stats: List[quotes.reflect.Statement]): Set[String] = {
    import quotes.reflect.*
    stats.collect {
      case vd: ValDef if vd.rhs.exists(rhs => isMatcher(rhs)) => vd.name
    }.toSet
  }

  /**
   * Inline matcher val defs back into the expression and strip them from the Block. Scala 3 hoists named args with matchers into Block val defs typed as Nothing, which causes:
   *   1. NPE/CCE when Nothing-typed null is used as a primitive param
   *   2. Double matcher registration (val registers once, Ident gets wrapped in DefaultMatcher) Solution: substitute Idents referencing matcher vals with their original rhs
   *      expressions, then remove those val defs from the Block stats.
   */
  private[mockito] def inlineMatcherValDefs(using
      Quotes
  )(
      stats: List[quotes.reflect.Statement],
      expr: quotes.reflect.Term,
      matcherValNames: Set[String]
  ): (List[quotes.reflect.Statement], quotes.reflect.Term) = {
    import quotes.reflect.*
    if (matcherValNames.isEmpty) (stats, expr)
    else {
      // Build substitution map: val name → rhs expression
      val substitutions: Map[String, Term] = stats.collect {
        case vd: ValDef if matcherValNames.contains(vd.name) && vd.rhs.isDefined =>
          vd.name -> vd.rhs.get
      }.toMap

      // Remove matcher val defs from stats
      val remainingStats = stats.filterNot {
        case vd: ValDef => matcherValNames.contains(vd.name)
        case _          => false
      }

      // Substitute Idents in the expression with the original rhs
      val substituted = new TreeMap {
        override def transformTerm(tree: Term)(owner: Symbol): Term = tree match {
          case Ident(name) if substitutions.contains(name) =>
            substitutions(name)
          case NamedArg(name, Ident(valName)) if substitutions.contains(valName) =>
            NamedArg(name, substitutions(valName))
          case _ => super.transformTerm(tree)(owner)
        }
      }.transformTerm(expr)(Symbol.spliceOwner)

      (remainingStats, substituted)
    }
  }

  /**
   * Handle the Block case shared by both WhenMacro and DoSomethingMacro transformInvocation: detect and inline matcher val defs, merge matcher val names, recurse into the fixed
   * expression, and reconstruct the Block if any non-matcher stats remain.
   */
  private[mockito] def transformBlock(using
      Quotes
  )(
      stats: List[quotes.reflect.Statement],
      expr: quotes.reflect.Term,
      matcherValNames: Set[String]
  )(recurse: (quotes.reflect.Term, Set[String]) => quotes.reflect.Term): quotes.reflect.Term = {
    import quotes.reflect.*
    val detectedMatcherVals         = detectMatcherValDefs(stats)
    val (remainingStats, fixedExpr) = inlineMatcherValDefs(stats, expr, detectedMatcherVals)
    val allMatcherVals              = matcherValNames ++ detectedMatcherVals
    val transformed                 = recurse(fixedExpr, allMatcherVals)
    if (remainingStats.nonEmpty) Block(remainingStats, transformed) else transformed
  }

  /** Determine package name for cats/scalaz based on class name */
  private[mockito] def packageName(using Quotes)(className: String): String =
    if (className.contains("Scalaz")) "scalaz" else "cats"

  /** Determine class name for cats/scalaz based on class name */
  private[mockito] def className(using Quotes)(className: String, start: String): String =
    if (className.contains("Scalaz")) start + "Scalaz" else start + "Cats"
}
