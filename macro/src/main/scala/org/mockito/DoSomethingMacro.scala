package org.mockito

import org.mockito.Utils._
import org.mockito.stubbing.Answer

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object DoSomethingMacro {

  def returnedBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.DoSomethingOps[$r]($v).willBe($_.returned).by[$_]($obj.$method[..$targs](...$args))" =>
          if (args.exists(a => hasMatchers(c)(a))) {
            val newArgs = args.map(a => transformArgs(c)(a))
            q"org.mockito.MockitoSugar.doReturn[$r]($v).when($obj).$method[..$targs](...$newArgs)"
          } else
            q"org.mockito.MockitoSugar.doReturn[$r]($v).when($obj).$method[..$targs](...$args)"

        case q"$_.DoSomethingOps[$r]($v).willBe($_.returned).by[$_]($obj.$method[..$targs])" =>
          q"org.mockito.MockitoSugar.doReturn[$r]($v).when($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def answeredBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.DoSomethingOps[$r]($v).willBe($_.answered).by[$_]($obj.$method[..$targs](...$args))" =>
          if (args.exists(a => hasMatchers(c)(a))) {
            val newArgs = args.map(a => transformArgs(c)(a))
            q"org.mockito.MockitoSugar.doAnswer($v).when($obj).$method[..$targs](...$newArgs)"
          } else
            q"org.mockito.MockitoSugar.doAnswer($v).when($obj).$method[..$targs](...$args)"

        case q"$_.DoSomethingOps[$r]($v).willBe($_.answered).by[$_]($obj.$method[..$targs])" =>
          q"org.mockito.MockitoSugar.doAnswer($v).when($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }

  def thrownBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    val r = c.Expr[T] {
      c.macroApplication match {
        case q"$_.ThrowSomethingOps[$_]($v).willBe($_.thrown).by[$_]($obj.$method[..$targs](...$args))" =>
          if (args.exists(a => hasMatchers(c)(a))) {
            val newArgs = args.map(a => transformArgs(c)(a))
            q"org.mockito.MockitoSugar.doThrow($v).when($obj).$method[..$targs](...$newArgs)"
          } else
            q"org.mockito.MockitoSugar.doThrow($v).when($obj).$method[..$targs](...$args)"

        case q"$_.ThrowSomethingOps[$_]($v).willBe($_.thrown).by[$_]($obj.$method[..$targs])" =>
          q"org.mockito.MockitoSugar.doThrow($v).when($obj).$method[..$targs]"

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
        case q"$_.theRealMethod.willBe($_.called).by[$_]($obj.$method[..$targs](...$args))" =>
          if (args.exists(a => hasMatchers(c)(a))) {
            val newArgs = args.map(a => transformArgs(c)(a))
            q"org.mockito.MockitoSugar.doCallRealMethod.when($obj).$method[..$targs](...$newArgs)"
          } else
            q"org.mockito.MockitoSugar.doCallRealMethod.when($obj).$method[..$targs](...$args)"

        case q"$_.theRealMethod.willBe($_.called).by[$_]($obj.$method[..$targs])" =>
          q"org.mockito.MockitoSugar.doCallRealMethod.when($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }
}
