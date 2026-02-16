package org.mockito

import org.mockito.MacroConstants.{ isSpecs2Matcher, MockitoMatchers }

import scala.reflect.macros.blackbox

object Utils {
  private[mockito] def hasMatchers(c: blackbox.Context)(args: List[c.Tree]): Boolean =
    args.exists(arg => isMatcher(c)(arg))

  private[mockito] def isMatcher(c: blackbox.Context)(arg: c.Tree): Boolean = {
    import c.universe.*
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
    import c.universe.*
    if (isMatcher(c)(arg)) arg
    else
      arg match {
        case a if a.toString.startsWith("x$") => a
        case q"$a: _*"                        => q"_root_.org.mockito.matchers.DefaultMatcher($a): _*"
        case q"$a"                            => q"_root_.org.mockito.matchers.DefaultMatcher($a)"
      }
  }

  private[mockito] def packageName(c: blackbox.Context)(cls: c.TermName): c.TermName = {
    import c.universe.*
    if (cls.toString.contains("Scalaz")) TermName("scalaz") else TermName("cats")
  }

  private[mockito] def className(c: blackbox.Context)(cls: c.TermName, start: String): c.TermName = {
    import c.universe.*
    if (cls.toString.contains("Scalaz")) TermName(start + "Scalaz") else TermName(start + "Cats")
  }
}
