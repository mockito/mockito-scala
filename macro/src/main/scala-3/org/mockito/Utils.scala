package org.mockito

import scala.quoted.*

/**
 * Shared Scala 3 macro tree utilities used by macro implementations across packages.
 */
object Utils {

  /** Find the type of the single constructor parameter of a value class, or abort with a macro error */
  private[mockito] def findValueClassParamType(using Quotes)(tpe: quotes.reflect.TypeRepr): quotes.reflect.TypeRepr = {
    import quotes.reflect.*
    tpe.typeSymbol.primaryConstructor.paramSymss.flatten
      .collectFirst { case p if p.isTerm => tpe.memberType(p).widen }
      .getOrElse(report.errorAndAbort(s"Could not find constructor parameter for value class ${tpe.show}"))
  }

  /** Extract type argument trees from an applied type, or Nil for non-generic types */
  private[mockito] def typeArgTrees(using Quotes)(tpe: quotes.reflect.TypeRepr): List[quotes.reflect.TypeTree] = {
    import quotes.reflect.*
    tpe match {
      case AppliedType(_, args) => args.map(a => TypeTree.of(using a.asType))
      case _                    => Nil
    }
  }
}
