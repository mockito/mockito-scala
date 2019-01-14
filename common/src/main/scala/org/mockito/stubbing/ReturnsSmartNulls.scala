package org.mockito.stubbing
import java.lang.reflect.Modifier.isFinal

import org.mockito.Mockito.mock
import org.mockito.internal.debugging.LocationImpl
import org.mockito.internal.exceptions.Reporter.smartNullPointerException
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues
import org.mockito.internal.util.ObjectMethodsGuru.isToStringMethod
import org.mockito.invocation.{ InvocationOnMock, Location }
import ru.vyarus.java.generics.resolver.GenericsResolver.resolve

object ReturnsSmartNulls extends DefaultAnswer {

  val delegate = new ReturnsMoreEmptyValues

  override def apply(invocation: InvocationOnMock): Option[Any] = Option(delegate.answer(invocation)).orElse {
    val method     = invocation.getMethod
    val context    = resolve(invocation.getMock.getClass).`type`(method.getDeclaringClass)
    val returnType = context.method(method).resolveReturnClass()
    if (!returnType.isPrimitive && !isFinal(returnType.getModifiers))
      Some(mock(returnType, ThrowsSmartNullPointer(invocation)))
    else
      None
  }

  case class ThrowsSmartNullPointer(unstubbedInvocation: InvocationOnMock, location: Location = new LocationImpl) extends Answer[AnyRef] {
    override def answer(invocation: InvocationOnMock): AnyRef =
      if (isToStringMethod(invocation.getMethod))
        s"""SmartNull returned by this unstubbed method call on a mock:
           |$unstubbedInvocation""".stripMargin
      else
        throw smartNullPointerException(unstubbedInvocation.toString, location)
  }
}
