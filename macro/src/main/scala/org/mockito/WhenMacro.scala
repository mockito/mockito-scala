package org.mockito

import org.mockito.stubbing.ScalaFirstStubbing

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object WhenMacro {

  def materialise[T: c.WeakTypeTag](c: blackbox.Context)(expr: c.Expr[T]): c.Expr[ScalaFirstStubbing[T]] = {
    import c.universe._

    def isMatcher(arg: Tree): Boolean =
      if (arg.toString().contains("org.mockito.matchers.ValueClassMatchers")) true
      else
        arg match {
          case q"$_.anyList[$_]"     => true
          case q"$_.anySeq[$_]"      => true
          case q"$_.anyIterable[$_]" => true
          case q"$_.anySet[$_]"      => true
          case q"$_.anyMap[$_, $_]"  => true
          case q"$_.any[$_]"         => true
          case q"$_.*[$_]"           => true
          case q"$_.anyByte"         => true
          case q"$_.anyBoolean"      => true
          case q"$_.anyChar"         => true
          case q"$_.anyDouble"       => true
          case q"$_.anyInt"          => true
          case q"$_.anyFloat"        => true
          case q"$_.anyShort"        => true
          case q"$_.anyLong"         => true

          case q"$_.isNull[$_]"    => true
          case q"$_.isNotNull[$_]" => true

          case q"$_.eqTo[$_]($_)"      => true
          case q"$_.same[$_]($_)"      => true
          case q"$_.isA[$_]($_)"       => true
          case q"$_.refEq[$_]($_, $_)" => true

          case q"$_.function0[$_]($_)" => true

          case q"$_.matches[$_]($_)"    => true
          case q"$_.startsWith[$_]($_)" => true
          case q"$_.contains[$_]($_)"   => true
          case q"$_.endsWith[$_]($_)"   => true

          case q"$_.argThat[$_]($_)"     => true
          case q"$_.byteThat[$_]($_)"    => true
          case q"$_.booleanThat[$_]($_)" => true
          case q"$_.charThat[$_]($_)"    => true
          case q"$_.doubleThat[$_]($_)"  => true
          case q"$_.intThat[$_]($_)"     => true
          case q"$_.floatThat[$_]($_)"   => true
          case q"$_.shortThat[$_]($_)"   => true
          case q"$_.longThat[$_]($_)"    => true

          case _ => false
        }

    def transformArg(arg: Tree): Tree =
      if (isMatcher(arg)) arg
      else
        arg match {
          case q"$a" => q"org.mockito.matchers.EqMatchers.eqTo($a)"
        }

    val t = weakTypeOf[T]

    c.Expr[ScalaFirstStubbing[T]] {
      expr.tree match {
        case q"$obj.$method(..$args)" =>
          if (args.exists(a => isMatcher(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(a))
            q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method(..$newArgs)))"
          } else
            q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method(..$args)))"

        case q"$obj.$method[..$tagrs](..$args)" =>
          if (args.exists(a => isMatcher(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(a))
            q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method[..$tagrs](..$newArgs)))"
          } else
            q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method[..$tagrs](..$args)))"

        case q"$obj.$method" => q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[$t]($obj.$method))"

        case q"$obj.$method[..$tagrs]" => q"new org.mockito.stubbing.ScalaFirstStubbing(org.mockito.Mockito.when[..$tagrs]($obj.$method))"

        case o =>
          println("other", show(o))
          ???
      }
    }
  }

}
