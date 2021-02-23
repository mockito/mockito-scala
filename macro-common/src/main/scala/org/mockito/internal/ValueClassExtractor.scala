package org.mockito.internal

import org.mockito.internal.MacroDebug.debugResult
import org.mockito.internal.ScalaVersion.{ V2_11, V2_12, V2_13 }

import scala.reflect.macros.blackbox

trait ValueClassExtractor[VC] extends Serializable {
  def isValueClass: Boolean = true
  def extract(vc: VC): Any
  def extractAs[T](vc: VC): T = extract(vc).asInstanceOf[T]
}

class NormalClassExtractor[T] extends ValueClassExtractor[T] {
  override def isValueClass: Boolean = false
  override def extract(vc: T): Any   = vc
}

class ReflectionExtractor[VC] extends ValueClassExtractor[VC] {
  override def extract(vc: VC): Any = {
    val constructorParam = vc.getClass.getConstructors.head.getParameters.head
    val accessor = vc.getClass.getMethods
      .filter(m => m.getName == constructorParam.getName || m.getName.endsWith("$$" + constructorParam.getName))
      .head
    accessor.setAccessible(true)
    accessor.invoke(vc)
  }
}

object ValueClassExtractor {
  def apply[T: ValueClassExtractor]: ValueClassExtractor[T] = implicitly[ValueClassExtractor[T]]

  implicit def instance[VC]: ValueClassExtractor[VC] = macro materialise[VC]

  def materialise[VC: c.WeakTypeTag](c: blackbox.Context): c.Expr[ValueClassExtractor[VC]] = {
    import c.universe._
    val tpe          = weakTypeOf[VC]
    val typeSymbol   = tpe.typeSymbol
    val isValueClass = typeSymbol.isClass && typeSymbol.asClass.isDerivedValueClass

    val r =
      if (isValueClass) {
        ScalaVersion.Current match {
          case V2_12 | V2_13 =>
            c.Expr[ValueClassExtractor[VC]](q"new _root_.org.mockito.internal.ReflectionExtractor[$tpe]")
          case V2_11 =>
            c.Expr[ValueClassExtractor[VC]] {
              val companion = typeSymbol.companion

              if (companion.info.decls.exists(_.name.toString == "unapply"))
                q"""
                  new _root_.org.mockito.internal.ValueClassExtractor[$tpe] {
                    override def extract(vc: $tpe): Any = $companion.unapply(vc).get
                  }
                 """
              else
                q"new _root_.org.mockito.internal.NormalClassExtractor[$tpe]"
            }
        }
      } else
        c.Expr[ValueClassExtractor[VC]](q"new _root_.org.mockito.internal.NormalClassExtractor[$tpe]")

    debugResult(c)("mockito-print-extractor")(r.tree)

    r
  }
}
