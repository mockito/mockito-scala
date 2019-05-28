package org.mockito.internal

import scala.collection.immutable.ArraySeq

package object handler {
  def unwrapVarargs(args: Array[Any]): Array[Any] =
    args.lastOption match {
      case Some(arg: ArraySeq[_]) => args.init ++ arg
      case _                      => args
    }
}
