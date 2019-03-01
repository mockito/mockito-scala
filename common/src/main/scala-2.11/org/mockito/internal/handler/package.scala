package org.mockito.internal

import org.mockito.internal.invocation.MockitoMethod

import scala.collection.mutable

package object handler {
  def unwrapVarargs(method: MockitoMethod, args: Array[Any]): Array[Any] =
    args.flatMap[Any, Array[Any]] {
      case a: mutable.WrappedArray[_] => a.array
      case other                      => Array[Any](other)
    }
}
