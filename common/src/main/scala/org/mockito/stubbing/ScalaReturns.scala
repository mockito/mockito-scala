package org.mockito
package stubbing

import org.mockito.internal.ValueClassExtractor
import org.mockito.internal.exceptions.Reporter.{ cannotStubVoidMethodWithAReturnValue, wrongTypeOfReturnValue }
import org.mockito.internal.stubbing.answers.InvocationInfo
import org.mockito.invocation.InvocationOnMock

case class ScalaReturns[T: ValueClassExtractor](value: T) extends ScalaAnswer[T] with ValidableAnswer with Serializable {
  override def answer(invocation: InvocationOnMock): T =
    if (ValueClassExtractor[T].isValueClass) {
      if (invocation.returnType == classOf[Object])
        value
      else
        ValueClassExtractor[T].extractAs[T](value)
    } else value

  override def validateFor(invocation: InvocationOnMock): Unit = {
    val invocationInfo = new InvocationInfo(invocation)
    if (invocationInfo.isVoid) {
      throw cannotStubVoidMethodWithAReturnValue(invocationInfo.getMethodName)
    }

    if (value == null && invocationInfo.returnsPrimitive) {
      throw wrongTypeOfReturnValue(invocationInfo.printMethodReturnType, "null", invocationInfo.getMethodName)
    }
  }

  override def toString = s"Returns: $value"
}
