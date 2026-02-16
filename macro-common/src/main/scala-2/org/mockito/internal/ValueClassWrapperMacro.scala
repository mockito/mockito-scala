package org.mockito.internal

import scala.reflect.macros.blackbox

/**
 * Scala 2 macro implementation for ValueClassWrapper.
 */
trait ValueClassWrapperCompat {
  implicit def instance[VC]: ValueClassWrapper[VC] = macro ValueClassWrapperMacro.materialise[VC]
}

object ValueClassWrapperMacro {
  def materialise[VC: c.WeakTypeTag](c: blackbox.Context): c.Expr[ValueClassWrapper[VC]] = {
    import c.universe.*
    val tpe          = weakTypeOf[VC]
    val typeSymbol   = tpe.typeSymbol
    val isValueClass = typeSymbol.isClass && typeSymbol.asClass.isDerivedValueClass

    val r =
      if (isValueClass)
        c.Expr[ValueClassWrapper[VC]](q"new _root_.org.mockito.internal.ReflectionWrapper[$tpe]")
      else
        c.Expr[ValueClassWrapper[VC]](q"new _root_.org.mockito.internal.NormalClassWrapper[$tpe]")

    MacroDebug.debugResult(c)("mockito-print-wrapper")(r.tree)

    r
  }
}
