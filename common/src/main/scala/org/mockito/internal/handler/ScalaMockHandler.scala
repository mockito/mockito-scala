package org.mockito
package internal.handler

import java.lang.reflect.Method
import java.lang.reflect.Modifier.isAbstract
import java.util.concurrent.ConcurrentHashMap

import org.mockito.internal.handler.ScalaMockHandler._
import org.mockito.internal.invocation.{ InterceptedInvocation, MockitoMethod }
import org.mockito.invocation.{ Invocation, MockHandler }
import org.mockito.mock.MockCreationSettings
import org.scalactic.TripleEquals._

class ScalaMockHandler[T](mockSettings: MockCreationSettings[T]) extends MockHandlerImpl[T](mockSettings) {
  override def handle(invocation: Invocation): AnyRef =
    if (invocation.getMethod.getName.contains("$default$") && !isAbstract(invocation.getMethod.getModifiers))
      invocation.callRealMethod()
    else {
      val scalaInvocation = invocation match {
        case i: InterceptedInvocation =>
          val mockitoMethod: MockitoMethod = readField(i, "mockitoMethod")
          new InterceptedInvocation(
            readField(i, "mockRef"),
            mockitoMethod,
            unwrapByNameArgs(mockitoMethod, i.getRawArguments.asInstanceOf[Array[Any]]).asInstanceOf[Array[Object]],
            readField(i, "realMethod"),
            i.getLocation,
            i.getSequenceNumber
          )
        case other => other
      }
      super.handle(scalaInvocation)
    }
}

object ScalaMockHandler {
  def apply[T](mockSettings: MockCreationSettings[T]): MockHandler[T] =
    new InvocationNotifierHandler[T](new ScalaNullResultGuardian[T](new ScalaMockHandler(mockSettings)), mockSettings)

  private def readField[T](invocation: InterceptedInvocation, field: String): T = {
    val f = classOf[InterceptedInvocation].getDeclaredField(field)
    f.setAccessible(true)
    f.get(invocation).asInstanceOf[T]
  }

  private def unwrapByNameArgs(method: MockitoMethod, args: Array[Any]): Array[Any] =
    Extractors
      .getOrDefault(method.getJavaMethod.getDeclaringClass, ArgumentExtractor.Empty)
      .transformArgs(method.getJavaMethod, args)

  val Extractors = new ConcurrentHashMap[Class[_], ArgumentExtractor]

  case class ArgumentExtractor(toTransform: Seq[(Method, Set[Int])]) {
    def transformArgs(method: Method, args: Array[Any]): Array[Any] =
      toTransform
        .find(_._1 === method)
        .map(_._2)
        .map { transformIndices =>
          args.zipWithIndex.map {
            case (arg: Function0[_], idx) if transformIndices.contains(idx) => arg()
            case (arg, _)                                                   => arg
          }
        }
        .getOrElse(args)
  }

  object ArgumentExtractor {
    val Empty = ArgumentExtractor(Seq.empty)
  }
}
