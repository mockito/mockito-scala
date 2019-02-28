package org.mockito.internal

import org.mockito.internal.invocation.MockitoMethod

import scala.collection.immutable.ArraySeq

package object handler {
  def unwrapVarargs(method: MockitoMethod, args: Array[Any]): Array[Any] =
    args.toIterable.flatMap {
      case a: ArraySeq[_] => a.unsafeArray
      case other          => Array[Any](other)
    }.toArray
}
