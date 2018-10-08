package org.mockito

import org.mockito.verification.VerificationMode
import org.mockito.Utils._

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

object Called {
  def by[T](stubbing: T): T = macro DoSomethingMacro.calledBy[T]
}

object VerifyMacro {

  def wasMacro[T: c.WeakTypeTag](c: blackbox.Context)(called: c.Expr[Called.type])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method[..$targs](..$args)).was($_.called)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verify($obj).$method[..$targs](..$newArgs)"
          } else
            q"$order.verify($obj).$method[..$targs](..$args)"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).was($_.called)($order)" =>
          q"$order.verify($obj).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  def wasNotMacro[T: c.WeakTypeTag](c: blackbox.Context)(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method[..$targs](..$args)).was($_.never).called($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.never).$method[..$targs](..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.never).$method[..$targs](..$args)"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).was($_.never).called($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.never).$method[..$targs]"

        case q"$_.StubbingOps[$_]($obj).was($_.never).called($_)" =>
          q"org.mockito.InternalMockitoSugar.verifyNoMoreInteractions($obj)"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }



  case class Times(times: Int)

  def wasMacroTimes[T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[Times])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method[..$targs](..$args)).wasCalled($times)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.times($times.times)).$method[..$targs](..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.times($times.times)).$method[..$targs](..$args)"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).wasCalled($times)($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.times($times.times)).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  case class AtLeast(times: Int)

  def wasMacroAtLeast[T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[AtLeast])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method[..$targs](..$args)).wasCalled($times)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.atLeast($times.times)).$method[..$targs](..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.atLeast($times.times)).$method[..$targs](..$args)"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).wasCalled($times)($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.atLeast($times.times)).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  case class AtMost(times: Int)

  def wasMacroAtMost[T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[AtMost])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method[..$targs](..$args)).wasCalled($times)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.atMost($times.times)).$method[..$targs](..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.atMost($times.times)).$method[..$targs](..$args)"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).wasCalled($times)($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.atMost($times.times)).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  class OnlyOn

  def wasMacroOnlyOn[T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[OnlyOn])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method[..$targs](..$args)).wasCalled($_)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.only).$method[..$targs](..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.only).$method[..$targs](..$args)"

        case q"$_.StubbingOps[$_]($obj.$method[..$targs]).wasCalled($_)($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.only).$method[..$targs]"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }
}

sealed trait VerifyOrder {
  def verify[T](mock: T): T
  def verifyWithMode[T](mock: T, mode: VerificationMode): T
}

object VerifyUnOrdered extends VerifyOrder {
  override def verify[T](mock: T): T                                 = Mockito.verify(mock)
  override def verifyWithMode[T](mock: T, mode: VerificationMode): T = Mockito.verify(mock, mode)
}

case class VerifyInOrder(mocks: Seq[AnyRef]) extends VerifyOrder {
  private val _inOrder = Mockito.inOrder(mocks: _*)

  override def verify[T](mock: T): T                                 = _inOrder.verify(mock)
  override def verifyWithMode[T](mock: T, mode: VerificationMode): T = _inOrder.verify(mock, mode)
  def verifyNoMoreInteractions(): Unit                               = _inOrder.verifyNoMoreInteractions()
}

object VerifyOrder {
  implicit val unOrdered: VerifyOrder = VerifyUnOrdered
}
