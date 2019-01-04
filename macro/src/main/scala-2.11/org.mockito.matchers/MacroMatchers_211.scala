package org.mockito.matchers

import org.scalactic.Equality
import org.mockito.ArgumentMatcher
import org.mockito.internal.ValueClassExtractor
import org.mockito.{ArgumentMatcher, ArgumentMatchers => JavaMatchers}

import scala.collection.mutable
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object MacroMatchers_211 {

  def eqTo[T](value: T)(implicit $eq: Equality[T]): T = {
    ThatMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = $eq.areEqual(value, v)
      override def toString: String       = s"eqTo($value)"
    })
    value
  }

  //TODO try to remove this duplicated method
  def eqToWithExtractor[T](value: T, others: T*)(implicit $eq: Equality[T], $vce: ValueClassExtractor[T]): T = {
    val rawValues: Seq[T] = Seq(value) ++ others
    JavaMatchers.argThat(new ArgumentMatcher[T] {
      override def matches(v: T): Boolean = v match {
        case a: mutable.WrappedArray[_] if rawValues.length == a.length =>
          (rawValues zip a) forall {
            case (expected, got) => $eq.areEqual(expected.asInstanceOf[T], got)
          }
        case other =>
          $eq.areEqual($vce.extract(value).asInstanceOf[T], other)
      }
      override def toString: String = s"eqTo(${rawValues.mkString(", ")})"
    })
    value
  }

  def eqToMatcher[T: c.WeakTypeTag](c: blackbox.Context)(value: c.Expr[T], others: c.Expr[T]*)(eq: c.Tree): c.Expr[T] = {
    import c.universe._

    def isValueClass(tpe: Tree) = tpe.symbol.isClass && tpe.symbol.asClass.isDerivedValueClass

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.eqTo[$tpe](new $clazz($arg))($_)" if isValueClass(tpe) =>
          q"new $clazz(_root_.org.mockito.matchers.MacroMatchers_211.eqTo($arg))"

        case q"$_.eqTo[$tpe](..$arg)($_)" =>
          q"_root_.org.mockito.matchers.MacroMatchers_211.eqToWithExtractor[$tpe](..$arg)"

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
