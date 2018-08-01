package org.mockito

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

trait ValueClassMatchers[T] {

  def anyVal: T

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

    val r = c.Expr[ValueClassMatchers[T]] { q"""
      new org.mockito.ValueClassMatchers[$tpe] {
        override def anyVal: $tpe = new $tpe(org.mockito.ArgumentMatchers.any[$paramType]())
      }
    """ }
    println(show(r))
    r
  }
}
