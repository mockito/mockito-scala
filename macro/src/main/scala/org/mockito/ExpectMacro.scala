package org.mockito

import scala.reflect.macros.blackbox

object ExpectMacro extends VerificationMacroTransformer {

  def callsTo[R](c: blackbox.Context)(stubbedMethodCall: c.Tree)(order: c.Expr[VerifyOrder]): c.Expr[R] = {
    import c.universe._

    val r = c.Expr[R](transformExpectation(c)(c.macroApplication))
    if (c.settings.contains("mockito-print-expect")) {
      val pos = s"${c.enclosingPosition.source.file.name}:${c.enclosingPosition.line}"
      println(pos + " " + show(r.tree))
    }

    r
  }

  private def transformExpectation[R](c: blackbox.Context)(called: c.Tree): c.Tree = {
    import c.universe._

    called match {
      case q"$_.this.expect.no($_).to($obj.$methodOrField)($order)" =>
        val calledPattern = show(q"$obj.$methodOrField")
        q"""if (!_root_.org.mockito.MockitoSugar.mockingDetails($obj).isMock)
              throw new _root_.org.mockito.exceptions.misusing.MissingMethodInvocationException(Seq(
                "'expect no calls to <?>' requires an argument which is 'a method call on a mock',",
                "  but [" + $calledPattern + "] is a mock object.",
                "",
                "The following would be correct (note the usage of 'calls to' vs 'calls on'):",
                "    expect no calls to " + $calledPattern + ".bar(*)",
                "    expect no calls on " + $calledPattern,
                ""
              ).mkString("\n"))
            else ${transformInvocation(c)(q"$obj.$methodOrField", order, q"${c.prefix}.mode")}
         """

      case q"$_.this.expect.$_($_).to($obj.$method[..$targs](...$args))($order)" =>
        transformInvocation(c)(q"$obj.$method[..$targs](...$args)", order, q"${c.prefix}.mode")

      case q"$_.this.expect.$_($_).to($call)($order)" =>
        transformInvocation(c)(call, order, q"${c.prefix}.mode")

      case q"$_.expect($mode).to($call)($order)" =>
        transformInvocation(c)(call, order, mode)

      case _ => throw new Exception(s"Expect-to macro: couldn't recognize invocation ${show(called)}")
    }
  }

  def callsOn[R](c: blackbox.Context)(mock: c.Tree): c.Expr[R] = {
    import c.universe._

    val r = c.Expr[R](transformNoInteractionsExpectation(c)(c.macroApplication))
    if (c.settings.contains("mockito-print-expect")) {
      val pos = s"${c.enclosingPosition.source.file.name}:${c.enclosingPosition.line}"
      println(pos + " " + show(r.tree))
    }

    r
  }

  def transformNoInteractionsExpectation[R](c: blackbox.Context)(called: c.Tree): c.Tree = {
    import c.universe._

    called match {
      case q"$_.this.expect.no($_.calls).on($obj)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyZeroInteractions($obj))"

      case q"$_.this.expect.noMore($_.calls).on($obj)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions($obj))"

      case q"$_.this.expect.noMore($_.calls.apply($_.ignoringStubs)).on($obj)" =>
        q"verification(_root_.org.mockito.MockitoSugar.verifyNoMoreInteractions(_root_.org.mockito.MockitoSugar.ignoreStubs($obj): _*))"

      case _ => throw new Exception(s"Expect-on macro: couldn't recognize invocation ${show(called)}")
    }
  }

}
