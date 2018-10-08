package org.mockito

import org.mockito.Utils._
import org.mockito.stubbing.Answer

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object DoSomethingMacro {

  def returnedBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    c.Expr[T] {
      c.macroApplication match {
        case q"$_.DoSomethingOps[$r]($v).willBe($_.returned).by[$_]($obj.$method[..$targs](..$args))" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"org.mockito.InternalMockitoSugar.doReturn[$r]($v).when($obj).$method[..$targs](..$newArgs)"
          } else
            q"org.mockito.InternalMockitoSugar.doReturn[$r]($v).when($obj).$method[..$targs](..$args)"

        case q"$_.DoSomethingOps[$r]($v).willBe($_.returned).by[$_]($obj.$method[..$targs])" =>
          q"org.mockito.InternalMockitoSugar.doReturn[$r]($v).when($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  def answeredBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    c.Expr[T] {
      c.macroApplication match {
        case q"$_.DoSomethingOps[$r]($v).willBe($_.answered).by[$_]($obj.$method[..$targs](..$args))" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"org.mockito.Mockito.doAnswer(org.mockito.DoSomethingMacro.argumentToAnswer($v)).when($obj).$method[..$targs](..$newArgs)"
          } else
            q"org.mockito.Mockito.doAnswer(org.mockito.DoSomethingMacro.argumentToAnswer($v)).when($obj).$method[..$targs](..$args)"

        case q"$_.DoSomethingOps[$r]($v).willBe($_.answered).by[$_]($obj.$method[..$targs])" =>
          q"org.mockito.Mockito.doAnswer(org.mockito.DoSomethingMacro.argumentToAnswer($v)).when($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  def thrownBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    c.Expr[T] {
      c.macroApplication match {
        case q"$_.ThrowSomethingOps[$_]($v).willBe($_.thrown).by[$_]($obj.$method[..$targs](..$args))" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"org.mockito.InternalMockitoSugar.doThrow($v).when($obj).$method[..$targs](..$newArgs)"
          } else
            q"org.mockito.InternalMockitoSugar.doThrow($v).when($obj).$method[..$targs](..$args)"

        case q"$_.ThrowSomethingOps[$_]($v).willBe($_.thrown).by[$_]($obj.$method[..$targs])" =>
          q"org.mockito.InternalMockitoSugar.doThrow($v).when($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  def calledBy[T: c.WeakTypeTag](c: blackbox.Context)(stubbing: c.Expr[T]): c.Expr[T] = {
    import c.universe._

    c.Expr[T] {
      c.macroApplication match {
        case q"$_.theRealMethod.willBe($_.called).by[$_]($obj.$method[..$targs](..$args))" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"org.mockito.InternalMockitoSugar.doCallRealMethod.when($obj).$method[..$targs](..$newArgs)"
          } else
            q"org.mockito.InternalMockitoSugar.doCallRealMethod.when($obj).$method[..$targs](..$args)"

        case q"$_.theRealMethod.willBe($_.called).by[$_]($obj.$method[..$targs])" =>
          q"org.mockito.InternalMockitoSugar.doCallRealMethod.when($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  def argumentToAnswer(v: Any): Answer[Any] = v match {
    case f: Function0[_]                                => invocationToAnswer(_ => f())
    case f: Function1[_, _]                             => functionToAnswer(f)
    case f: Function2[_, _, _]                          => functionToAnswer(f)
    case f: Function3[_, _, _, _]                       => functionToAnswer(f)
    case f: Function4[_, _, _, _, _]                    => functionToAnswer(f)
    case f: Function5[_, _, _, _, _, _]                 => functionToAnswer(f)
    case f: Function6[_, _, _, _, _, _, _]              => functionToAnswer(f)
    case f: Function7[_, _, _, _, _, _, _, _]           => functionToAnswer(f)
    case f: Function8[_, _, _, _, _, _, _, _, _]        => functionToAnswer(f)
    case f: Function9[_, _, _, _, _, _, _, _, _, _]     => functionToAnswer(f)
    case f: Function10[_, _, _, _, _, _, _, _, _, _, _] => functionToAnswer(f)
    case other                                          => invocationToAnswer(_ => other)
  }
}
