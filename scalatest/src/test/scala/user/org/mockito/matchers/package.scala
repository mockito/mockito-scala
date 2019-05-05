package user.org.mockito

package object matchers {
  class ValueClass(private val v: String)    extends AnyVal
  case class ValueCaseClass private (v: Int) extends AnyVal

  case class Baz(param1: String, param2: String)

  class Foo {
    def bar[T](v: T): T = v

    def barTyped(v: String): String = v

    def barByte(v: Byte): Byte = v

    def barBoolean(v: Boolean): Boolean = v

    def barChar(v: Char): Char = v

    def barDouble(v: Double): Double = v

    def barInt(v: Int): Int = v

    def barFloat(v: Float): Float = v

    def barShort(v: Short): Short = v

    def barLong(v: Long): Long = v

    def barList[T](v: List[T]): List[T] = v

    def barSeq[T](v: Seq[T]): Seq[T] = v

    def barIterable[T](v: Iterable[T]): Iterable[T] = v

    def barMap[K, V](v: Map[K, V]): Map[K, V] = v

    def barSet[T](v: Set[T]): Set[T] = v

    def valueClass(v: ValueClass): String = ???

    def valueCaseClass(v: ValueCaseClass): Int = ???

    def baz(v: Baz): Baz = v

    def iHaveFunction0[T](v: () => T): T = v()

    def pepe[N](n: N, v: String = "meh"): N = ???
  }
}
