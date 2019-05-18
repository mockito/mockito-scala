package org.mockito.matchers

import org.mockito.matchers.MacroMatchers.anyValMatcher
import org.mockito.{ ArgumentMatchers => JavaMatchers }

import scala.reflect.macros.blackbox

trait AnyMatcher[T] {
  def any: T
}

class AnyMatcherStandard[T] extends AnyMatcher[T] { override def any: T = JavaMatchers.any[T]() }

object AnyMatcher {
  implicit def default[T]: AnyMatcher[T] = macro anyValMatcher[T]
}

object MacroMatchers {
  def anyValMatcher[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[AnyMatcher[T]] = {
    import c.universe._
    val tpe              = weakTypeOf[T]
    val typeSymbol       = tpe.typeSymbol
    val isValueClass     = typeSymbol.isClass && typeSymbol.asClass.isDerivedValueClass
    val isCaseValueClass = isValueClass && typeSymbol.asClass.isCaseClass

    lazy val innerType =
      tpe.members
        .filter(_.isConstructor)
        .flatMap(_.asMethod.paramLists)
        .flatMap(_.map(_.typeSignature))
        .head

    val r = if (isCaseValueClass) c.Expr[AnyMatcher[T]] {
      q"""
      new _root_.org.mockito.matchers.AnyMatcher[$tpe] {
        override def any: $tpe = ${tpe.companion.decl(TermName("apply"))}(_root_.org.mockito.ArgumentMatchers.any[$innerType]())
      }
    """
    } else if (isValueClass) c.Expr[AnyMatcher[T]] {
      q"""
      new _root_.org.mockito.matchers.AnyMatcher[$tpe] {
        override def any: $tpe = new $tpe(_root_.org.mockito.ArgumentMatchers.any[$innerType]())
      }
    """
    } else
      c.Expr[AnyMatcher[T]](q"new _root_.org.mockito.matchers.AnyMatcherStandard[$tpe]")

    if (c.settings.contains("mockito-print-matcher")) println(show(r.tree))
    r
  }
}
