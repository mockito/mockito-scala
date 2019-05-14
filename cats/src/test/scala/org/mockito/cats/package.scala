package org.mockito

package object cats {

  case class Error(e: String)

  case class ValueClass(s: String) extends AnyVal

  trait Foo {
    def returnsOptionString(v: String): Option[String]

    def returnsOptionT[T](v: T): Option[T]

    def returnsMT[M[_], T](v: T): M[T]

    def shouldI(should: Boolean): String
  }

}
