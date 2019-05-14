package org.mockito.cats

import cats.Eq
import org.mockito._
import org.scalactic.Equality

trait MockitoCats extends ScalacticSerialisableHack {

  def whenF[F[_], T](methodCall: F[T]): CatsStubbing[F, T] = Mockito.when(methodCall)

  implicit def catsEquality[T: Eq]: Equality[T] = new EqToEquality[T]
}

object MockitoCats extends MockitoCats
