package org.mockito
package scalaz

import _root_.scalaz.{ Applicative, Equal, MonadError }
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Stubber
import org.scalactic.Equality

import scala.reflect.ClassTag

trait MockitoScalaz extends ScalacticSerialisableHack {

  def whenF[F[_], T](methodCall: F[T]): ScalazStubbing[F, T] = Mockito.when(methodCall)

  def whenFG[F[_], G[_], T](methodCall: F[G[T]]): ScalazStubbing2[F, G, T] = Mockito.when(methodCall)

  def doReturnF[F[_]: Applicative, T](toBeReturned: T, toBeReturnedNext: T*): Stubber =
    Mockito.doReturn(
      Applicative[F].pure(toBeReturned),
      toBeReturnedNext.map(Applicative[F].pure(_)).map(_.asInstanceOf[Object]): _*
    )

  def doReturnFG[F[_]: Applicative, G[_]: Applicative, T](toBeReturned: T, toBeReturnedNext: T*): Stubber =
    Mockito.doReturn(
      Applicative[F].compose[G].pure(toBeReturned),
      toBeReturnedNext.map(Applicative[F].compose[G].pure(_)).map(_.asInstanceOf[Object]): _*
    )

  def doFailWith[F[_], E, T](error: E, errors: E*)(implicit ae: MonadError[F, E]): Stubber =
    Mockito.doReturn(
      ae.raiseError[T](error),
      errors.map(e => ae.raiseError[T](e)).map(_.asInstanceOf[Object]): _*
    )

  def doFailWithG[F[_]: Applicative, G[_], E, T](error: E, errors: E*)(implicit ae: MonadError[G, E]): Stubber =
    Mockito.doReturn(
      Applicative[F].pure(ae.raiseError[T](error)),
      errors.map(e => ae.raiseError[T](e)).map(Applicative[F].pure(_)).map(_.asInstanceOf[Object]): _*
    )

  def doAnswerF[F[_]: Applicative, R](l: => R): Stubber =
    Mockito.doAnswer(invocationToAnswer(_ => {
      // Store the param so we don't evaluate the by-name twice
      val _l = l
      _l match {
        case f: Function0[_] => f()
        case _               => _l
      }
    }).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, R](f: P0 => R)(implicit classTag: ClassTag[P0] = defaultClassTag[P0]): Stubber = clazz[P0] match {
    case c if c == classOf[InvocationOnMock] =>
      Mockito.doAnswer(invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(Applicative[F].pure(_)))
    case _ =>
      Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  }
  def doAnswerF[F[_]: Applicative, P0, P1, R](f: (P0, P1) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, R](f: (P0, P1, P2) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, P3, R](f: (P0, P1, P2, P3) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, P3, P4, R](f: (P0, P1, P2, P3, P4) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, P3, P4, P5, R](f: (P0, P1, P2, P3, P4, P5) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, R](f: (P0, P1, P2, P3, P4, P5, P6) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, P7, R](f: (P0, P1, P2, P3, P4, P5, P6, P7) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, P7, P8, R](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, R](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))
  def doAnswerF[F[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].pure(_)))

  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, R](l: => R): Stubber =
    Mockito.doAnswer(invocationToAnswer(_ => {
      // Store the param so we don't evaluate the by-name twice
      val _l = l
      _l match {
        case f: Function0[_] => f()
        case _               => _l
      }
    }).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, R](f: P0 => R)(implicit classTag: ClassTag[P0] = defaultClassTag[P0]): Stubber =
    clazz[P0] match {
      case c if c == classOf[InvocationOnMock] =>
        Mockito.doAnswer(invocationToAnswer(i => f(i.asInstanceOf[P0])).andThen(Applicative[F].compose[G].pure(_)))
      case _ =>
        Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
    }
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, R](f: (P0, P1) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, R](f: (P0, P1, P2) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, P3, R](f: (P0, P1, P2, P3) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, P3, P4, R](f: (P0, P1, P2, P3, P4) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, P3, P4, P5, R](f: (P0, P1, P2, P3, P4, P5) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, R](f: (P0, P1, P2, P3, P4, P5, P6) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, P7, R](f: (P0, P1, P2, P3, P4, P5, P6, P7) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, P7, P8, R](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, R](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))
  def doAnswerFG[F[_]: Applicative, G[_]: Applicative, P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f).andThen(Applicative[F].compose[G].pure(_)))

  implicit def scalazEquality[T: Equal]: Equality[T] = new EqToEquality[T]
}

object MockitoScalaz extends MockitoScalaz
