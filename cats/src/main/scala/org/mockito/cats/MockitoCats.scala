package org.mockito.cats

import cats.Eq
import org.mockito._
import org.scalactic.Equality

import scala.reflect.ClassTag

trait MockitoCats extends MockitoSugar {

  def whenF[F[_], T](methodCall: F[T]): CatsStubbing[F, T] = Mockito.when(methodCall)

  implicit def catsEquality[T: ClassTag: Eq]: Equality[T] = new EqToEquality[T]
}

object MockitoCats extends MockitoCats
