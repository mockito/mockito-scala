package org.mockito

import scala.reflect.macros.blackbox

object ExpectMacro extends VerificationMacroTransformer {

  def to[R](c: blackbox.Context)(invocationOnMock: c.Tree)(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    import c.universe._

    val r   = c.Expr[R](transformExpectation(c)(c.macroApplication))
    val pos = s"${c.enclosingPosition.source.file.name}:${c.enclosingPosition.line}"
    if (c.settings.contains("mockito-print-expect")) println(pos + " " + show(r.tree))

    r
  }

  private def transformExpectation[R](c: blackbox.Context)(called: c.Tree): c.Tree = {
    import c.universe._

    called match {
      case q"$_.this.expect.no($_.calls).to($_.this.$obj)($_)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"
      case q"$_.this.expect.noMore($_.calls).to($_.this.$obj)($_)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"
      case q"$_.expect.noMore($_.calls.apply($_.ignoringStubs)).to($_.this.$obj)($_)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions(_root_.org.mockito.MockitoSugar.ignoreStubs($obj): _*))"

      case q"$_.this.expect.$_($_).to($obj.$method[..$targs](...$args))($order)" =>
        transformInvocation(c)(q"$obj.$method[..$targs](...$args)", order, q"${c.prefix}.mode")

      case q"$_.this.expect.no($_.calls).to($obj)($_)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"
      case q"$_.this.expect.noMore($_.calls).to($obj)($_)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"
      case q"$_.expect.noMore($_.calls.apply($_.ignoringStubs)).to($obj)($_)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions(_root_.org.mockito.MockitoSugar.ignoreStubs($obj): _*))"

      case q"$_.this.expect.$_($_).to($call)($order)" =>
        transformInvocation(c)(call, order, q"${c.prefix}.mode")

      case q"$_.expect($mode).to($call)($order)" =>
        transformInvocation(c)(call, order, mode)

      case _ => throw new Exception(s"Expect macro: couldn't recognize invocation ${show(called)}")
    }
  }
}
