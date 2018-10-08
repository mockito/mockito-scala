package org.mockito
import scala.reflect.macros.blackbox

object Utils {
  private[mockito] def isMatcher(c: blackbox.Context)(arg: c.Tree): Boolean = {
    import c.universe._
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

        case q"$_.Captor.asCapture[$_]($_)"    => true

        case _ => false
      }
  }

  private[mockito] def transformArg(c: blackbox.Context)(arg: c.Tree): c.Tree = {
    import c.universe._
    if (isMatcher(c)(arg)) arg
    else
      arg match {
        case q"$a" => q"org.mockito.matchers.EqMatchers.eqTo($a)"
      }
  }
}
