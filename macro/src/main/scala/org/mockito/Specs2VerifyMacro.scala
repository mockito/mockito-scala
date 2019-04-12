package org.mockito

import org.mockito.Utils._

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

object Specs2VerifyMacro {

  val WordsToNumbers = Map(
    "no"    -> 0,
    "one"   -> 1,
    "two"   -> 2,
    "three" -> 3
  )

  val WasWere = "(was|were)".r.pattern

  def wasMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(calls: c.Expr[T])(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    import c.universe._

    val r = c.Expr[R] {
      c.macroApplication match {

        case q"$_.there.$w[$_]($_.$numWord[$_]($obj).$method[..$targs](...$args))($order)" if WasWere.matcher(w.toString).matches =>
          val times   = WordsToNumbers.getOrElse(numWord.toString, 1)
          val newArgs = args.map(a => transformArgs(c)(a))
          q"verification($order.verifyWithMode($obj, _root_.org.mockito.IdiomaticMockitoBase.Times($times)).$method[..$targs](...$newArgs))"

        case q"$_.there.$w[$_]($_.$mode[$_]($times)($obj).$method[..$targs](...$args))($order)" if WasWere.matcher(w.toString).matches =>
          val effectiveMode = TermName(mode.toString.capitalize)
          val newArgs       = args.map(a => transformArgs(c)(a))
          q"verification($order.verifyWithMode($obj, _root_.org.mockito.IdiomaticMockitoBase.$effectiveMode($times)).$method[..$targs](...$newArgs))"

        case q"$_.there.$w[$_]($_.noCallsTo[$_]($obj))($_)" if WasWere.matcher(w.toString).matches =>
          q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"

        case q"$_.there.$w[$_]($_.noMoreCallsTo[$_]($obj))($_)" if WasWere.matcher(w.toString).matches =>
          q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"

        case q"$_.there.$w[$_]($_.Specs2IntOps($times).times[$_]($obj).$method[..$targs](...$args))($order)"
            if WasWere.matcher(w.toString).matches =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"verification($order.verifyWithMode($obj, _root_.org.mockito.IdiomaticMockitoBase.Times(1)).$method[..$targs](...$newArgs))"

        case q"$_.MatchResultOps[$_]($prev).andThen[$_]($_.$numWord[$_]($obj).$method[..$targs](...$args))($order)" =>
          val times   = WordsToNumbers.getOrElse(numWord.toString, 1)
          val newArgs = args.map(a => transformArgs(c)(a))
          q"$prev and verification($order.verifyWithMode($obj, _root_.org.mockito.IdiomaticMockitoBase.Times($times)).$method[..$targs](...$newArgs))"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-verify")) println(show(r.tree))
    r
  }
}
