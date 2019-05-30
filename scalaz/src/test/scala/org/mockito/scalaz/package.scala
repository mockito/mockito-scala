package org.mockito

import _root_.scalaz._

import scala.concurrent.Future

package object scalaz {

  type ErrorOr[A] = Either[Error, A]

  case class Error(e: String)

  case class ValueClass(s: String) extends AnyVal

  trait Foo {
    def returnsOptionString(v: String): Option[String]

    def returnsGenericOption[T](v: T): Option[T]

    def returnsMT[M[_], T](v: T): M[T]

    def shouldI(should: Boolean): String

    def returnsFuture(v: String): Future[ValueClass]

    def returnsFutureEither(v: String): Future[ErrorOr[ValueClass]]

    def returnsEitherT(v: String): EitherT[Future, Error, ValueClass]

    def returnsOptionT(v: String): OptionT[List, ValueClass]
  }

}
