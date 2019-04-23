package org.mockito

import org.mockito.Utils._

import scala.reflect.macros.blackbox

object DoSomethingMacro {

  def returnedBy[T: c.WeakTypeTag, S](c: blackbox.Context)(stubbing: c.Expr[T])($ev: c.Expr[S]): c.Expr[S] = {
    import c.universe._

    val r = c.Expr[S] {
      c.macroApplication match {
        case q"$_.DoSomethingOps[$_]($v).willBe($_.returned).by[$_]($obj.$method[..$targs](...$args))($_)" =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"_root_.org.mockito.MockitoSugar.doReturn($v).when($obj).$method[..$targs](...$newArgs)"

        case q"$_.DoSomethingOps[$_]($v).willBe($_.returned).by[$_]($obj.$method[..$targs])($_)" =>
          q"_root_.org.mockito.MockitoSugar.doReturn($v).when($obj).$method[..$targs]"

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
        case q"$_.$cls[..$_]($v).willBe($_.answered).by[$_]($obj.$method[..$targs](...$args))($_)"
            if cls.toString.startsWith("DoSomethingOps") =>
          val newArgs = args.map(a => transformArgs(c)(a))
          q"_root_.org.mockito.MockitoSugar.doAnswer($v).when($obj).$method[..$targs](...$newArgs)"

        case q"$_.$cls[..$_]($v).willBe($_.answered).by[$_]($obj.$method[..$targs])($_)" if cls.toString.startsWith("DoSomethingOps") =>
          q"_root_.org.mockito.MockitoSugar.doAnswer($v).when($obj).$method[..$targs]"

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
          val newArgs = args.map(a => transformArgs(c)(a))
          q"_root_.org.mockito.MockitoSugar.doThrow($v).when($obj).$method[..$targs](...$newArgs)"

        case q"$_.ThrowSomethingOps[$_]($v).willBe($_.thrown).by[$_]($obj.$method[..$targs])" =>
          q"_root_.org.mockito.MockitoSugar.doThrow($v).when($obj).$method[..$targs]"

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
          val newArgs = args.map(a => transformArgs(c)(a))
          q"_root_.org.mockito.MockitoSugar.doCallRealMethod.when($obj).$method[..$targs](...$newArgs)"

        case q"$_.theRealMethod.willBe($_.called).by[$_]($obj.$method[..$targs])" =>
          q"_root_.org.mockito.MockitoSugar.doCallRealMethod.when($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
    if (c.settings.contains("mockito-print-do-something")) println(show(r.tree))
    r
  }
}
