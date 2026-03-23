package org.mockito.matchers

import org.mockito.Utils
import scala.quoted.*

/**
 * Scala 3 macro implementation for DefaultValueProvider.
 */
trait DefaultValueProviderCompat {
  inline implicit def default[T]: DefaultValueProvider[T] = ${ DefaultValueProviderMacro.defaultValueProviderImpl[T] }
}

object DefaultValueProviderMacro {
  def defaultValueProviderImpl[T: Type](using Quotes): Expr[DefaultValueProvider[T]] = {
    import quotes.reflect.*

    val tpe        = TypeRepr.of[T]
    val typeSymbol = tpe.typeSymbol

    val isCaseValueClass = tpe.baseClasses.exists(_.fullName == "scala.AnyVal") &&
      typeSymbol.flags.is(Flags.Case)

    if (isCaseValueClass) {
      Utils.findValueClassParamType(tpe).asType match {
        case '[innerT] =>
          val innerDefaultExpr = '{ DefaultValueProvider.defaultProvider[innerT].default }
          val typeArgTrees     = Utils.typeArgTrees(tpe)
          val constructorRef   = Select(New(TypeIdent(typeSymbol)), typeSymbol.primaryConstructor)
          val constructorCall  =
            if (typeArgTrees.nonEmpty)
              Apply(TypeApply(constructorRef, typeArgTrees), List(innerDefaultExpr.asTerm))
            else
              Apply(constructorRef, List(innerDefaultExpr.asTerm))
          '{ new DefaultValueProvider[T] { override def default: T = ${ constructorCall.asExprOf[T] } } }
      }
    } else {
      '{ DefaultValueProvider.defaultProvider[T] }
    }
  }
}
