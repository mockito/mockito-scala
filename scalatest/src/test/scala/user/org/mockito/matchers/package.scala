package user.org.mockito

package object matchers {
  class ValueClass(private val v: String) extends AnyVal
  case class ValueCaseClass(v: Int)       extends AnyVal
}
