package org.mockito.internal

import scala.collection.immutable.ArraySeq

package object handler {
  def unwrapVarargs(args: Array[Any]): Array[Any] =
    args.toIterable.flatMap {
      case a: ArraySeq[_] => a.unsafeArray
      case other          => Array[Any](other)
    }.toArray
}
