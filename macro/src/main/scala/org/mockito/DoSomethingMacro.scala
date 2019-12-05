package org.mockito

import org.mockito.Utils._

import scala.reflect.macros.blackbox

object DoSomethingMacro {
  def returnedBy[T: c.WeakTypeTag, S](c: blackbox.Context)(stubbing: c.Expr[T])($ev: c.Expr[S]): c.Expr[S] = {
    import c.universe._

    val r = c.Expr[S] {
      c.macroApplication match {
        case q"$_.DoSomethingOps[$_]($v).willBe($_.returned).by[$_]($invocation)($_)" =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.MockitoSugar.doReturn($v)")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def returnedF[T: c.WeakTypeTag, S](c: blackbox.Context)(stubbing: c.Expr[T])(F: c.Tree, $ev: c.Expr[S]): c.Expr[S] = {
    import c.universe._

    val r = c.Expr[S] {
      c.macroApplication match {
        case q"$_.$cls[$_]($v).willBe($_.returnedF).by[$f, $s]($invocation)(..$_)" if cls.toString.startsWith("DoSomethingOps") =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "Mockito")}.doReturnF[$f, $s]($v)")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def returnedFG[T: c.WeakTypeTag, S](c: blackbox.Context)(stubbing: c.Expr[T])(F: c.Tree, G: c.Tree, $ev: c.Expr[S]): c.Expr[S] = {
    import c.universe._

    val r = c.Expr[S] {
      c.macroApplication match {
        case q"$_.$cls[$_]($v).willBe($_.returnedFG).by[$f, $g, $s]($invocation)(..$_)" if cls.toString.startsWith("DoSomethingOps") =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "Mockito")}.doReturnFG[$f, $g, $s]($v)")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def answeredBy[T: c.WeakTypeTag, S](c: blackbox.Context)(stubbing: c.Expr[T])($ev: c.Expr[S]): c.Expr[S] = {
    import c.universe._

    val r = c.Expr[S] {
      c.macroApplication match {
        case q"$_.$cls[..$_]($v).willBe($_.answered).by[$_]($invocation)($_)" if cls.toString.startsWith("DoSomethingOps") =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.MockitoSugar.doAnswer($v)")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def answeredF[T: c.WeakTypeTag, S](c: blackbox.Context)(stubbing: c.Expr[T])(F: c.Tree, $ev: c.Expr[S]): c.Expr[S] = {
    import c.universe._

    val r = c.Expr[S] {
      c.macroApplication match {
        case q"$_.$cls[..$ftargs]($v).willBe($_.answeredF).by[$f, $_]($invocation)(..$_)" if cls.toString.startsWith("DoSomethingOps") =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "Mockito")}.doAnswerF[$f, ..$ftargs]($v)")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def answeredFG[T: c.WeakTypeTag, S](c: blackbox.Context)(stubbing: c.Expr[T])(F: c.Tree, G: c.Tree, $ev: c.Expr[S]): c.Expr[S] = {
    import c.universe._

    val r = c.Expr[S] {
      c.macroApplication match {
        case q"$_.$cls[..$ftargs]($v).willBe($_.answeredFG).by[$f, $g, $_]($invocation)(..$_)" if cls.toString.startsWith("DoSomethingOps") =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "Mockito")}.doAnswerFG[$f, $g, ..$ftargs]($v)")

        case o => throw new Exception(s"Couldn't recognize answeredFG ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def thrownBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T])($ev: c.Tree): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.ThrowSomethingOps[$_]($v).willBe($_.thrown).by[$_]($invocation)($_)" =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.MockitoSugar.doThrow($v)")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def raised[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T])(F: c.Tree): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.$cls[$e]($v).willBe($_.raised).by[$f, $t]($invocation)(..$_)" if cls.toString.startsWith("DoSomethingOps") =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "Mockito")}.doFailWith[$f, $e, $t]($v)")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def raisedG[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T])(F: c.Tree, G: c.Tree): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.$cls[$e]($v).willBe($_.raisedG).by[$f, $g, $t]($invocation)(..$_)" if cls.toString.startsWith("DoSomethingOps") =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.${packageName(c)(cls)}.${className(c)(cls, "Mockito")}.doFailWithG[$f, $g, $e, $t]($v)")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def calledBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.theRealMethod.willBe($_.called).by[$_]($invocation)" =>
          transformInvocation(c)(invocation, q"_root_.org.mockito.MockitoSugar.doCallRealMethod")

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  private def transformInvocation(c: blackbox.Context)(invocation: c.Tree, action: c.Tree): c.Tree = {
    import c.universe._

    val pf: PartialFunction[c.Tree, c.Tree] = {
      case q"$obj.$method[..$targs](...$args)" =>
        val newArgs = args.map(a => transformArgs(c)(a))
        q"$action.when($obj).$method[..$targs](...$newArgs)"
      case q"$obj.$method[..$targs]" =>
        q"$action.when($obj).$method[..$targs]"
    }

    if (pf.isDefinedAt(invocation))
      pf(invocation)
    else if (pf.isDefinedAt(invocation.children.last)) {
      val values = invocation.children
        .dropRight(1)
        .collect {
          case q"$_ val $name:$_ = $value" => name.toString -> value.asInstanceOf[c.Tree]
        }
        .toMap

      val nonMatchers = invocation.children.dropRight(1).collect {
        case t @ q"$_ val $_:$_ = $value" if !isMatcher(c)(value) => t
      }

      invocation.children.last match {
        case q"$obj.$method[..$targs](...$args)" =>
          val newArgs = args.map { a =>
            transformArgs(c)(a).map {
              case p if show(p).startsWith("x$") => transformArg(c)(values(p.toString))
              case other                         => other
            }
          }
          q"..$nonMatchers; $action.when($obj).$method[..$targs](...$newArgs)"
      }
    } else throw new Exception(s"Couldn't recognize invocation ${show(invocation)}")
  }
}
