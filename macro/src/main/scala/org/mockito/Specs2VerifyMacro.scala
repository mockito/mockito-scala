package org.mockito

import org.mockito.Utils._
import org.mockito.internal.MacroDebug.debugResult

import scala.reflect.macros.blackbox

object Specs2VerifyMacro extends VerificationMacroTransformer {
  private val WordsToNumbers = Map(
    "no"    -> 0,
    "one"   -> 1,
    "two"   -> 2,
    "three" -> 3
  )

  private val WasWere = "(was|were)".r.pattern

  def wasMacro[T: c.WeakTypeTag, R](c: blackbox.Context)(calls: c.Expr[T])(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    import c.universe._

    val transformSpecs2Verification: PartialFunction[c.Tree, c.Tree] = {
      case q"$_.$numWord[$_]($obj).$method[..$targs](...$args)" =>
        val times   = WordsToNumbers.getOrElse(numWord.toString, 1)
        val newArgs = args.map(a => transformArgs(c)(a))
        q"verification($order.verifyWithMode($obj, _root_.org.mockito.IdiomaticMockitoBase.Times($times)).$method[..$targs](...$newArgs))"

      case q"$_.$mode[$_]($times)($obj).$method[..$targs](...$args)" =>
        val effectiveMode = TermName(mode.toString.capitalize)
        val newArgs       = args.map(a => transformArgs(c)(a))
        q"verification($order.verifyWithMode($obj, _root_.org.mockito.IdiomaticMockitoBase.$effectiveMode($times)).$method[..$targs](...$newArgs))"

      case q"$_.noCallsTo[$_]($obj)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"

      case q"$_.noMoreCallsTo[$_]($obj)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"

      case q"$_.Specs2IntOps($times).times[$_]($obj).$method[..$targs](...$args)" =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"verification($order.verifyWithMode($obj, _root_.org.mockito.IdiomaticMockitoBase.Times($times)).$method[..$targs](...$newArgs))"
    }

    val r = c.Expr[R] {
      c.macroApplication match {
        case q"$_.there.$w[$_]($t)($order)" if WasWere.matcher(w.toString).matches && transformSpecs2Verification.isDefinedAt(t) =>
          transformSpecs2Verification(t)

        case q"$_.MatchResultOps[$_]($prev).andThen[$_]($t)($_)" =>
          def transform(tree: c.Tree): c.Tree = tree match {
            case q"$_.VerifyingOps[$_]($_).$_($_)($_)"              => transformVerification(c)(tree)
            case _ if transformSpecs2Verification.isDefinedAt(tree) => transformSpecs2Verification(tree)
            case other                                              => other
          }

          q"${transform(prev)} and ${transform(t)}"

        case q"$_.got[$_]({..$block})($order)" =>
          block.foldLeft(q"") {
            case (q"", t) => if (transformSpecs2Verification.isDefinedAt(t)) transformSpecs2Verification(t) else q"$t"
            case (other, t) =>
              if (transformSpecs2Verification.isDefinedAt(t)) q"$other and ${transformSpecs2Verification(t)}" else q"$other and $t"
          }

        case o => throw new Exception(s"Specs2VerifyMacro: Couldn't recognize ${show(o)}")
      }
    }
    debugResult(c)("mockito-print-verify")(r.tree)
    r
  }
}
