package org.mockito
import scala.reflect.macros.blackbox
import scala.util.matching.Regex

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
    "$eq$tilde", // =~
    "Captor.asCapture",
    "capture"
  )

  private val specs2implicits: Regex = "(matcher)?[t,T]o(Partial)?FunctionCall(\\d*)".r
  private def isSpecs2Matcher(methodName: String): Boolean = specs2implicits.pattern.matcher(methodName).matches

  private[mockito] def isMatcher(c: blackbox.Context)(arg: c.Tree): Boolean = {
    import c.universe._
    if (arg.toString().contains("org.mockito.matchers.MacroMatchers")) true
    else {
      val methodName = arg match {
        case q"$_.Captor.asCapture[$_]($_)" => Some("Captor.asCapture")
        case q"$_.n.$methodName[$_](...$_)" => Some(methodName.toString)
        case q"$_.$methodName"              => Some(methodName.toString)
        case q"$_.$methodName[..$_]"        => Some(methodName.toString)
        case q"$_.$methodName(...$_)"       => Some(methodName.toString)
        case q"$_.$methodName[..$_](...$_)" => Some(methodName.toString)

        case _ => None
      }
      methodName.exists(mn => MockitoMatchers.contains(mn) || isSpecs2Matcher(mn))
    }

  }

  private[mockito] def transformArgs(c: blackbox.Context)(args: List[c.Tree]): List[c.Tree] =
    args.map(arg => transformArg(c)(arg))

  private[mockito] def transformArg(c: blackbox.Context)(arg: c.Tree): c.Tree = {
    import c.universe._
    if (isMatcher(c)(arg)) arg
    else
      arg match {
        case q"$a" => q"_root_.org.mockito.matchers.DefaultMatcher.defaultMatcher($a)"
      }
  }
}
