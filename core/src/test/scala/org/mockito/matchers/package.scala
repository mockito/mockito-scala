package org.mockito

package object matchers {
  class ValueClass(val v: String)   extends AnyVal
  case class ValueCaseClass(v: Int) extends AnyVal
}
