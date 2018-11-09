package org.mockito.matchers

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object ValueClassMatchers {

  def eqToValMatcher[T: c.WeakTypeTag](c: blackbox.Context)(value: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.eqToVal[$_]($clazz($arg))" => q"$clazz(org.mockito.ArgumentMatchersSugar.eqTo($arg))"
        case q"$_.eqToVal[$_](new $clazz($arg))" => q"new $clazz(org.mockito.ArgumentMatchersSugar.eqTo($arg))"
        case q"$_.eqToVal[$tpe]($arg)" =>
          val companion = q"$tpe".symbol.companion
          q"$companion.apply(org.mockito.ArgumentMatchersSugar.eqTo( $companion.unapply($arg).get ))"
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
        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-matcher")) println(show(r.tree))
    r
  }
}
