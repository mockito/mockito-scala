package org.mockito

import org.mockito.Utils.*
import org.mockito.internal.MacroDebug.debugResult
import org.mockito.stubbing.{ ScalaFirstStubbing, ScalaOngoingStubbing }

import scala.reflect.macros.blackbox

object WhenMacro {
  // Re-export runtime classes so existing code referencing WhenMacro.* continues to work
  type AnswerActions[T]   = WhenMacroRuntime.AnswerActions[T]
  type AnswerPFActions[T] = WhenMacroRuntime.AnswerPFActions[T]
  val RealMethod = WhenMacroRuntime.RealMethod

  private def transformInvocation(c: blackbox.Context)(invocation: c.Tree): c.Tree = {
    import c.universe.*

    val pf: PartialFunction[c.Tree, c.Tree] = {
      case q"$obj.$method[..$targs](...$args)" =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"$obj.$method[..$targs](...$newArgs)"
      case q"$obj.$method[..$targs]" => invocation
    }

    if (pf.isDefinedAt(invocation))
      pf(invocation)
    else if (pf.isDefinedAt(invocation.children.last)) {
      val vals       = invocation.children.dropRight(1)
      val valsByName = vals.collect { case line @ q"$_ val $name:$_ = $value" =>
        name.toString -> (value.asInstanceOf[c.Tree], line)
      }.toMap

      val inlinedArgsCall = invocation.children.last match {
        case q"$obj.$method[..$targs](...$args)" =>
          val newArgs = args.map { a =>
            transformArgs(c)(a).map {
              case p if show(p).startsWith("x$") => transformArg(c)(valsByName(p.toString)._1)
              case other                         => other
            }
          }
          q"$obj.$method[..$targs](...$newArgs)"
      }

      val call     = show(inlinedArgsCall)
      val usedVals = valsByName.collect {
        case (name, (_, line)) if call.contains(name) => line
      }

      q"..$usedVals; $inlinedArgsCall"
    } else throw new Exception(s"Couldn't recognize invocation ${show(invocation)}")
  }

  private val ShouldReturnOptions                                 = Set("shouldReturn", "mustReturn", "returns")
  private val FunctionalShouldReturnOptions                       = ShouldReturnOptions.map(_ + "F")
  private val FunctionalShouldReturnOptions2                      = ShouldReturnOptions.map(_ + "FG")
  def shouldReturn[T: c.WeakTypeTag](c: blackbox.Context): c.Tree = {
    import c.universe.*

    val r = c.macroApplication match {
      case q"$_.StubbingOps[$t]($invocation).$m" if ShouldReturnOptions.contains(m.toString) =>
        q"new _root_.org.mockito.IdiomaticMockitoBase.ReturnActions(_root_.org.mockito.Mockito.when[$t](${transformInvocation(c)(invocation)}))"

      case q"$_.$cls[..$_]($invocation).$m" if cls.toString.startsWith("StubbingOps") && FunctionalShouldReturnOptions.contains(m.toString) =>
        q"new _root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "IdiomaticMockito")}.ReturnActions(_root_.org.mockito.Mockito.when(${transformInvocation(c)(invocation)}))"

      case q"$_.$cls[..$_]($invocation).$m" if cls.toString.startsWith("StubbingOps2") && FunctionalShouldReturnOptions2.contains(m.toString) =>
        q"new _root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "IdiomaticMockito")}.ReturnActions2(_root_.org.mockito.Mockito.when(${transformInvocation(c)(invocation)}))"

      case o => throw new Exception(s"Couldn't recognize ${show(o)}")
    }
    debugResult(c)("mockito-print-when")(r)
    r
  }

  def isLenient[T: c.WeakTypeTag](c: blackbox.Context)(): c.Expr[Unit] = {
    import c.universe.*

    val r = c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($invocation).isLenient()" =>
          q"new _root_.org.mockito.stubbing.ScalaFirstStubbing(_root_.org.mockito.Mockito.when[$t](${transformInvocation(c)(invocation)})).isLenient()"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    debugResult(c)("mockito-print-lenient")(r.tree)
    r
  }

  val ShouldCallOptions                                                                                                          = Set("shouldCall", "mustCall", "calls")
  def shouldCallRealMethod[T: c.WeakTypeTag](c: blackbox.Context)(crm: c.Expr[RealMethod.type]): c.Expr[ScalaOngoingStubbing[T]] = {
    import c.universe.*

    val r = c.Expr[ScalaOngoingStubbing[T]] {
      c.macroApplication match {
        case q"$_.StubbingOps[$t]($invocation).$m($_.realMethod)" if ShouldCallOptions.contains(m.toString) =>
          q"new _root_.org.mockito.stubbing.ScalaOngoingStubbing(_root_.org.mockito.Mockito.when[$t](${transformInvocation(c)(invocation)}).thenCallRealMethod())"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    debugResult(c)("mockito-print-when")(r.tree)
    r
  }

  private val ShouldThrowOptions                                 = Set("shouldThrow", "mustThrow", "throws")
  private val FunctionalShouldFailOptions                        = Set("shouldFailWith", "mustFailWith", "failsWith", "raises")
  private val FunctionalShouldFailOptions2                       = Set("shouldFailWithG", "mustFailWithG", "failsWithG", "raisesG")
  def shouldThrow[T: c.WeakTypeTag](c: blackbox.Context): c.Tree = {
    import c.universe.*

    val r = c.macroApplication match {
      case q"$_.StubbingOps[$t]($invocation).$m" if ShouldThrowOptions.contains(m.toString) =>
        q"new _root_.org.mockito.IdiomaticMockitoBase.ThrowActions(_root_.org.mockito.Mockito.when[$t](${transformInvocation(c)(invocation)}))"

      case q"$_.$cls[..$_]($invocation).$m" if cls.toString.startsWith("StubbingOps") && FunctionalShouldFailOptions.contains(m.toString) =>
        q"new _root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "IdiomaticMockito")}.ThrowActions(_root_.org.mockito.Mockito.when(${transformInvocation(c)(invocation)}))"

      case q"$_.$cls[..$_]($invocation).$m" if cls.toString.startsWith("StubbingOps2") && FunctionalShouldFailOptions2.contains(m.toString) =>
        q"new _root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "IdiomaticMockito")}.ThrowActions2(_root_.org.mockito.Mockito.when(${transformInvocation(c)(invocation)}))"

      case o => throw new Exception(s"Couldn't recognize ${show(o)}")
    }
    debugResult(c)("mockito-print-when")(r)
    r
  }

  private val ShouldAnswerOptions                                 = Set("shouldAnswer", "mustAnswer", "answers")
  private val FunctionalShouldAnswerOptions                       = ShouldAnswerOptions.map(_ + "F")
  private val FunctionalShouldAnswerOptions2                      = ShouldAnswerOptions.map(_ + "FG")
  def shouldAnswer[T: c.WeakTypeTag](c: blackbox.Context): c.Tree = {
    import c.universe.*

    val r = c.macroApplication match {
      case q"$_.StubbingOps[$t]($invocation).$m" if ShouldAnswerOptions.contains(m.toString) =>
        q"new _root_.org.mockito.WhenMacroRuntime.AnswerActions(_root_.org.mockito.Mockito.when[$t](${transformInvocation(c)(invocation)}))"

      case q"$_.$cls[..$_]($invocation).$m" if cls.toString.startsWith("StubbingOps") && FunctionalShouldAnswerOptions.contains(m.toString) =>
        q"new _root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "IdiomaticMockito")}.AnswerActions(_root_.org.mockito.Mockito.when(${transformInvocation(c)(invocation)}))"

      case q"$_.$cls[..$_]($invocation).$m" if cls.toString.startsWith("StubbingOps2") && FunctionalShouldAnswerOptions2.contains(m.toString) =>
        q"new _root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "IdiomaticMockito")}.AnswerActions2(_root_.org.mockito.Mockito.when(${transformInvocation(c)(invocation)}))"

      case o => throw new Exception(s"Couldn't recognize ${show(o)}")
    }
    debugResult(c)("mockito-print-when")(r)
    r
  }

  private val ShouldAnswerPFOptions                                 = Set("shouldAnswerPF", "mustAnswerPF", "answersPF")
  def shouldAnswerPF[T: c.WeakTypeTag](c: blackbox.Context): c.Tree = {
    import c.universe.*

    val r = c.macroApplication match {
      case q"$_.StubbingOps[$t]($invocation).$m" if ShouldAnswerPFOptions.contains(m.toString) =>
        q"new _root_.org.mockito.WhenMacroRuntime.AnswerPFActions(_root_.org.mockito.Mockito.when[$t](${transformInvocation(c)(invocation)}))"

      case o => throw new Exception(s"Couldn't recognize ${show(o)}")
    }
    debugResult(c)("mockito-print-when")(r)
    r
  }
}
