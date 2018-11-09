package org.mockito.matchers

import org.mockito.ArgumentMatcher
import org.scalactic.Equality

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object MacroMatchers {

  def eqTo[T](value: T)(implicit $eq: Equality[T]): T = {
    ThatMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = $eq.areEqual(v, value)
      override def toString: String       = s"eqTo($value)"
    })
    value
  }

  def eqToMatcher[T: c.WeakTypeTag](c: blackbox.Context)(value: c.Expr[T])(eq: c.Tree): c.Expr[T] = {
    import c.universe._

    def isValueClass(tpe: Tree) = tpe.symbol.asClass.isDerivedValueClass

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.eqTo[$tpe]($clazz($arg))($_)" if isValueClass(tpe) =>
          q"$clazz(org.mockito.matchers.MacroMatchers.eqTo($arg))"

        case q"$_.eqTo[$tpe](new $clazz($arg))($_)" if isValueClass(tpe) =>
          q"new $clazz(org.mockito.matchers.MacroMatchers.eqTo($arg))"

        case q"$_.eqTo[$tpe]($arg)($_)" if isValueClass(tpe) && tpe.symbol.asClass.isCaseClass =>
          val companion = tpe.symbol.companion
          q"$companion.apply( org.mockito.matchers.MacroMatchers.eqTo( $companion.unapply($arg).get ))"

        case q"$_.eqTo[$tpe]($arg)($eq)" =>
          q"org.mockito.matchers.MacroMatchers.eqTo[$tpe]($arg)($eq)"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-matcher")) println(show(r.tree))
    r
  }

  def anyValMatcher[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.anyVal[$tpe]" => q"new $tpe(org.mockito.ArgumentMatchers.any())"
        case o                  => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-matcher")) println(show(r.tree))
    r
  }
}
