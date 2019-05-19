package org.mockito

import scala.concurrent.Future

package object cats {

  type ErrorOr[A] = Either[Error, A]

  case class Error(e: String)

  case class ValueClass(s: String) extends AnyVal

  trait Foo {
    def returnsOptionString(v: String): Option[String]

    def returnsOptionT[T](v: T): Option[T]

    def returnsMT[M[_], T](v: T): M[T]

    def shouldI(should: Boolean): String

    def returnsFuture(v: String): Future[ValueClass]

    def returnsFutureEither(v: String): Future[ErrorOr[ValueClass]]
  }

}
