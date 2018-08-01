package org.mockito.matchers

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

trait ValueClassMatchers[T] {

  def anyVal: T

  def eqToVal(v: Any): T

}

object ValueClassMatchers {

  implicit def materializeValueClassMatchers[T]: ValueClassMatchers[T] = macro materializeValueClassMatchersMacro[T]

  def materializeValueClassMatchersMacro[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[ValueClassMatchers[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]

    val param = tpe.decls
      .collectFirst {
        case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
      }
      .get
      .paramLists
      .head
      .head

    val paramType = tpe.decl(param.name).typeSignature.finalResultType

    c.Expr[ValueClassMatchers[T]] { q"""
      new org.mockito.matchers.ValueClassMatchers[$tpe] {
        override def anyVal: $tpe = new $tpe(org.mockito.ArgumentMatchers.any[$paramType]())
        override def eqToVal(v: Any): $tpe = new $tpe(org.mockito.ArgumentMatchers.eq[$paramType](v.asInstanceOf[$paramType]))
      }
    """ }
  }
}
