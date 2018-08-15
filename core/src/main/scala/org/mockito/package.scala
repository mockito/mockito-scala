package org

import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import scala.reflect.ClassTag

package object mockito {
  def clazz[T <: AnyRef](implicit classTag: ClassTag[T]): Class[T] = classTag.runtimeClass.asInstanceOf[Class[T]]

  def functionToAnswer[T](f: InvocationOnMock => T): Answer[T] = new Answer[T] {
    override def answer(invocation: InvocationOnMock): T = f(invocation)
  }
}
