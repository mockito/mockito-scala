package org.mockito
import scala.reflect.macros.blackbox

object Utils {
  private[mockito] def hasMatchers(c: blackbox.Context)(args: List[c.Tree]): Boolean =
    args.exists(arg => isMatcher(c)(arg))

  private val MockitoMatchers = Set(
    "anyByte",
    "anyBoolean",
    "anyChar",
    "anyDouble",
    "anyInt",
    "anyFloat",
    "anyShort",
    "anyLong",
    "anyList",
    "anySeq",
    "anyIterable",
    "anySet",
    "anyMap",
    "any",
    "anyVal",
    "$times", // *
    "isNull",
    "isNotNull",
    "eqTo",
    "eqToVal",
    "same",
    "isA",
    "refEq",
    "function0",
    "matches",
    "startsWith",
    "contains",
    "endsWith",
    "argThat",
    "byteThat",
    "booleanThat",
    "charThat",
    "doubleThat",
    "intThat",
    "floatThat",
    "shortThat",
    "longThat",
    "argMatching",
    "$greater", // >
    "$greater$eq", // >=
    "$less", // <
    "$less$eq", // <=
    "$eq$tilde" // =~
  )

  private[mockito] def isMatcher(c: blackbox.Context)(arg: c.Tree): Boolean = {
    import c.universe._
    if (arg.toString().contains("org.mockito.matchers.MacroMatchers")) true
    else
      arg match {
        case q"$_.Captor.asCapture[$_]($_)" => true
        case q"$_.capture" => true

        case q"$_.n.$methodName[$_](...$_)" => MockitoMatchers.contains(methodName.toString)
        case q"$_.$methodName"              => MockitoMatchers.contains(methodName.toString)
        case q"$_.$methodName[..$_]"        => MockitoMatchers.contains(methodName.toString)
        case q"$_.$methodName(...$_)"       => MockitoMatchers.contains(methodName.toString)
        case q"$_.$methodName[..$_](...$_)" => MockitoMatchers.contains(methodName.toString)

        case _ => false
      }
  }

  private[mockito] def transformArgs(c: blackbox.Context)(args: List[c.Tree]): List[c.Tree] =
    args.map(arg => transformArg(c)(arg))

  private[mockito] def transformArg(c: blackbox.Context)(arg: c.Tree): c.Tree = {
    import c.universe._
    if (isMatcher(c)(arg)) arg
    else
      arg match {
        case q"$a" => q"_root_.org.mockito.ArgumentMatchersSugar.eqTo($a)"
      }
  }
}
