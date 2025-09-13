package org.mockito.internal

import scala.collection.mutable

package object handler {
  def unwrapVarargs(args: Array[Any]): Array[Any] =
    args.lastOption match {
      case Some(arg: mutable.WrappedArray[?]) => args.init ++ arg
      case _                                  => args
    }
}
