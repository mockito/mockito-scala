package org
import scala.reflect.ClassTag

package object mockito {
  def clazz[T <: AnyRef](implicit classTag: ClassTag[T]): Class[T] = classTag.runtimeClass.asInstanceOf[Class[T]]
}
