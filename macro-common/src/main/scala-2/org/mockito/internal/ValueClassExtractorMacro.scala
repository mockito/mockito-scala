package org.mockito.internal

import org.mockito.internal.MacroDebug.debugResult

import scala.reflect.macros.blackbox

/**
 * Scala 2 macro implementation for ValueClassExtractor.
 */
trait ValueClassExtractorCompat {
  implicit def instance[VC]: ValueClassExtractor[VC] = macro ValueClassExtractorMacro.materialise[VC]
}

object ValueClassExtractorMacro {
  def materialise[VC: c.WeakTypeTag](c: blackbox.Context): c.Expr[ValueClassExtractor[VC]] = {
    import c.universe.*
    val tpe          = weakTypeOf[VC]
    val typeSymbol   = tpe.typeSymbol
    val isValueClass = typeSymbol.isClass && typeSymbol.asClass.isDerivedValueClass

    val r =
      if (isValueClass) {
        c.Expr[ValueClassExtractor[VC]](q"new _root_.org.mockito.internal.ReflectionExtractor[$tpe]")
      } else
        c.Expr[ValueClassExtractor[VC]](q"new _root_.org.mockito.internal.NormalClassExtractor[$tpe]")

    debugResult(c)("mockito-print-extractor")(r.tree)

    r
  }
}
