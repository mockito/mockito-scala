package org.mockito

import org.mockito.verification.VerificationMode
import org.mockito.Utils._

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

object VerifyMacro {

  def wasMacro[T: c.WeakTypeTag](c: blackbox.Context)()(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method(..$args)).wasCalled()($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verify($obj).$method(..$newArgs)"
          } else
            q"$order.verify($obj).$method(..$args)"

        case q"$_.StubbingOps[$_]($obj.$method).wasCalled()($order)" =>
          q"$order.verify($obj).$method"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  def wasNotMacro[T: c.WeakTypeTag](c: blackbox.Context)()(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method(..$args)).wasNotCalled()($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.never).$method(..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.never).$method(..$args)"

        case q"$_.StubbingOps[$_]($obj.$method).wasNotCalled()($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.never).$method"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  case class Times(times: Int)

  def wasMacroTimes[T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[Times])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method(..$args)).wasCalled($times)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.times($times.times)).$method(..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.times($times.times)).$method(..$args)"

        case q"$_.StubbingOps[$_]($obj.$method).wasCalled($times)($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.times($times.times)).$method"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  case class AtLeast(times: Int)

  def wasMacroAtLeast[T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[AtLeast])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method(..$args)).wasCalled($times)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.atLeast($times.times)).$method(..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.atLeast($times.times)).$method(..$args)"

        case q"$_.StubbingOps[$_]($obj.$method).wasCalled($times)($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.atLeast($times.times)).$method"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  case class AtMost(times: Int)

  def wasMacroAtMost[T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[AtMost])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method(..$args)).wasCalled($times)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.atMost($times.times)).$method(..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.atMost($times.times)).$method(..$args)"

        case q"$_.StubbingOps[$_]($obj.$method).wasCalled($times)($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.atMost($times.times)).$method"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }

  class OnlyOn

  def wasMacroOnlyOn[T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[OnlyOn])(order: c.Expr[org.mockito.VerifyOrder]): c.Expr[Unit] = {
    import c.universe._

    c.Expr[Unit] {
      c.macroApplication match {
        case q"$_.StubbingOps[$_]($obj.$method(..$args)).wasCalled($_)($order)" =>
          if (args.exists(a => isMatcher(c)(a))) {
            val newArgs: Seq[Tree] = args.map(a => transformArg(c)(a))
            q"$order.verifyWithMode($obj, org.mockito.Mockito.only).$method(..$newArgs)"
          } else
            q"$order.verifyWithMode($obj, org.mockito.Mockito.only).$method(..$args)"

        case q"$_.StubbingOps[$_]($obj.$method).wasCalled($_)($order)" =>
          q"$order.verifyWithMode($obj, org.mockito.Mockito.only).$method"

        case o => throw new Exception(s"Couldn't recognize ${show(o)}")
      }
    }
  }
}

trait VerifyOrder {
  def verify[T](mock: T): T
  def verifyWithMode[T](mock: T, mode: VerificationMode): T
}

object VerifyOrder {
  implicit val unOrdered: VerifyOrder = new VerifyOrder {
    override def verify[T](mock: T): T                                 = Mockito.verify(mock)
    override def verifyWithMode[T](mock: T, mode: VerificationMode): T = Mockito.verify(mock, mode)
  }

  def inOrder(mocks: Seq[AnyRef]): VerifyOrder = new VerifyOrder {
    private val _inOrder = Mockito.inOrder(mocks: _*)

    override def verify[T](mock: T): T                                 = _inOrder.verify(mock)
    override def verifyWithMode[T](mock: T, mode: VerificationMode): T = _inOrder.verify(mock, mode)
  }
}
