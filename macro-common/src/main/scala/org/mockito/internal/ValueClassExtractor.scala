package org.mockito.internal

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

trait ValueClassExtractor[VC] {
  def extract(vc: VC): Any
}

class NormalClassExtractor[T] extends ValueClassExtractor[T] {
  override def extract(vc: T): Any = vc
}

object ValueClassExtractor {

  implicit def instance[VC]: ValueClassExtractor[VC] = macro materialise[VC]

  def materialise[VC: c.WeakTypeTag](c: blackbox.Context): c.Expr[ValueClassExtractor[VC]] = {
    import c.universe._
    val tpe = weakTypeOf[VC]

    val isValueClass = tpe.typeSymbol.asClass.isDerivedValueClass

    val r = if (isValueClass) c.Expr[ValueClassExtractor[VC]] {
      val companion = tpe.typeSymbol.companion
      q"""
      new org.mockito.internal.ValueClassExtractor[$tpe] {
        override def extract(vc: $tpe): Any = $companion.unapply(vc).get
      }
    """
    } else
      c.Expr[ValueClassExtractor[VC]](q"new org.mockito.internal.NormalClassExtractor[$tpe]")

    if (c.settings.contains("mockito-print-extractor")) println(show(r.tree))

    r
  }
}
