package org.mockito.matchers

import org.mockito.internal.ValueClassExtractor
import org.mockito.{ ArgumentMatchers => JavaMatchers }
import org.scalactic.{ Equality, Prettifier }

import scala.reflect.macros.blackbox

object MacroMatchers_211 {

  def eqTo[T: Equality: ValueClassExtractor](value: T)(implicit $pt: Prettifier): T = {
    JavaMatchers.argThat(new EqTo[T](value))
    value
  }

  def eqToMatcher[T: c.WeakTypeTag](c: blackbox.Context)(value: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    def isValueClass(tpe: Tree) = tpe.symbol.isClass && tpe.symbol.asClass.isDerivedValueClass

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.eqTo[$tpe](new $clazz($arg))" if isValueClass(tpe) =>
          q"new $clazz(_root_.org.mockito.matchers.MacroMatchers_211.eqTo($arg))"

        case q"$_.eqTo[$tpe](..$arg)" =>
          q"_root_.org.mockito.matchers.MacroMatchers_211.eqTo[$tpe](..$arg)"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-matcher")) println(show(r.tree))
    r
  }

  def eqToValMatcher[T: c.WeakTypeTag](c: blackbox.Context)(value: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.eqToVal[$_]($clazz($arg))"     => q"$clazz(_root_.org.mockito.matchers.MacroMatchers_211.eqTo($arg))"
        case q"$_.eqToVal[$_](new $clazz($arg))" => q"new $clazz(_root_.org.mockito.matchers.MacroMatchers_211.eqTo($arg))"
        case q"$_.eqToVal[$tpe]($arg)" =>
          val companion = q"$tpe".symbol.companion
          q"$companion.apply(_root_.org.mockito.matchers.MacroMatchers_211.eqTo( $companion.unapply($arg).get ))"
        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-matcher")) println(show(r.tree))
    r
  }
}
