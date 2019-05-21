package org.mockito.cats

import cats.{ Applicative, ApplicativeError, Eq }
import org.mockito._
import org.mockito.stubbing.Stubber
import org.scalactic.Equality

trait MockitoCats extends ScalacticSerialisableHack {

  def whenF[F[_], T](methodCall: F[T]): CatsStubbing[F, T] = Mockito.when(methodCall)

  def whenFG[F[_], G[_], T](methodCall: F[G[T]]): CatsStubbing2[F, G, T] = Mockito.when(methodCall)

  def doReturnF[F[_]: Applicative, T](toBeReturned: T, toBeReturnedNext: T*): Stubber =
    Mockito.doReturn(
      Applicative[F].pure(toBeReturned),
      toBeReturnedNext.map(Applicative[F].pure).map(_.asInstanceOf[Object]): _*
    )

  def doReturnFG[F[_]: Applicative, G[_]: Applicative, T](toBeReturned: T, toBeReturnedNext: T*): Stubber =
    Mockito.doReturn(
      Applicative[F].compose[G].pure(toBeReturned),
      toBeReturnedNext.map(Applicative[F].compose[G].pure(_)).map(_.asInstanceOf[Object]): _*
    )

  def doFailWith[F[_], E, T](error: E, errors: E*)(implicit ae: ApplicativeError[F, E]): Stubber =
    Mockito.doReturn(
      ae.raiseError[T](error),
      errors.map(e => ae.raiseError[T](e)).map(_.asInstanceOf[Object]): _*
    )

  def doFailWithG[F[_]: Applicative, G[_], E, T](error: E, errors: E*)(implicit ae: ApplicativeError[G, E]): Stubber =
    Mockito.doReturn(
      Applicative[F].pure(ae.raiseError[T](error)),
      errors.map(e => ae.raiseError[T](e)).map(Applicative[F].pure).map(_.asInstanceOf[Object]): _*
    )

  implicit def catsEquality[T: Eq]: Equality[T] = new EqToEquality[T]
}

object MockitoCats extends MockitoCats
