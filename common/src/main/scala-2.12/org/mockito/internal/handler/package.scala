package org.mockito.internal

import scala.collection.mutable

package object handler {
  def unwrapVarargs(args: Array[Any]): Array[Any] =
    args.flatMap[Any, Array[Any]] {
      case a: mutable.WrappedArray[_] => a.array
      case other                      => Array[Any](other)
    }
}
