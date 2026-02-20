package org.mockito.matchers

import org.mockito.internal.MacroDebug.debugResult

import scala.reflect.macros.blackbox

/**
 * Scala 2 macro implementation for DefaultValueProvider.
 */
trait DefaultValueProviderCompat {
  implicit def default[T]: DefaultValueProvider[T] = macro DefaultValueProviderMacro._defaultValueProvider[T]
}

object DefaultValueProviderMacro {
  def _defaultValueProvider[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[DefaultValueProvider[T]] = {
    import c.universe.*
    val tpe          = weakTypeOf[T]
    val typeSymbol   = tpe.typeSymbol
    val isValueClass = typeSymbol.isClass && typeSymbol.asClass.isDerivedValueClass

    lazy val innerType =
      tpe.members
        .filter(_.isConstructor)
        .flatMap(_.asMethod.paramLists)
        .flatMap(_.map(_.typeSignature))
        .head

    val r =
      if (isValueClass) c.Expr[DefaultValueProvider[T]] {
        q"""
          new _root_.org.mockito.matchers.DefaultValueProvider[$tpe] {
            override def default: $tpe =
              new $tpe (
                _root_.org.mockito.matchers.DefaultValueProvider.defaultProvider[$innerType].default
              )
          }
        """
      }
      else
        c.Expr[DefaultValueProvider[T]](q"_root_.org.mockito.matchers.DefaultValueProvider.defaultProvider[$tpe]")

    debugResult(c)("mockito-print-matcher")(r.tree)
    r
  }
}
