package org.mockito
package internal.handler

import java.lang.reflect.Method
import java.lang.reflect.Modifier.isAbstract
import java.util.concurrent.ConcurrentHashMap

import org.mockito.internal.handler.ScalaMockHandler._
import org.mockito.internal.invocation._
import org.mockito.invocation.{ Invocation, MockHandler }
import org.mockito.mock.MockCreationSettings
import org.scalactic.Prettifier
import org.scalactic.TripleEquals._

class ScalaMockHandler[T](mockSettings: MockCreationSettings[T])(implicit $pt: Prettifier) extends MockHandlerImpl[T](mockSettings) {

  override def handle(invocation: Invocation): AnyRef =
    if (invocation.getMethod.getName.contains("$default$") && !isAbstract(invocation.getMethod.getModifiers))
      invocation.callRealMethod()
    else {
      val scalaInvocation = invocation match {
        case i: InterceptedInvocation =>
          val mockitoMethod = i.getMockitoMethod
          val rawArguments  = i.getRawArguments
          val arguments =
            if (rawArguments != null && rawArguments.nonEmpty && !isCallRealMethod)
              unwrapArgs(mockitoMethod, rawArguments.asInstanceOf[Array[Any]])
          else rawArguments

          new ScalaInvocation(i.getMockRef, mockitoMethod, arguments, rawArguments, i.getRealMethod, i.getLocation, i.getSequenceNumber)

        case other => other
      }
      super.handle(scalaInvocation)
    }
}

object ScalaMockHandler {
  def apply[T](mockSettings: MockCreationSettings[T])(implicit $pt: Prettifier): MockHandler[T] =
    new InvocationNotifierHandler[T](new ScalaNullResultGuardian[T](new ScalaMockHandler(mockSettings)), mockSettings)

  private def isCallRealMethod: Boolean =
    (new Exception).getStackTrace.toList.exists { t =>
      t.getClassName == "org.mockito.internal.handler.ScalaInvocation" &&
      t.getMethodName == "callRealMethod"
    }

  private def unwrapArgs(method: Method, args: Array[AnyRef]): Array[Object] =
    Extractors
      .getOrDefault(method.getDeclaringClass, ArgumentExtractor.Empty)
      .transformArgs(method, args.asInstanceOf[Array[Any]])
      .asInstanceOf[Array[Object]]

  val Extractors = new ConcurrentHashMap[Class[_], ArgumentExtractor]

  case class ArgumentExtractor(toTransform: Seq[(Method, Set[Int])]) {
    def transformArgs(method: Method, args: Array[Any]): Array[Any] = {
      val transformed = toTransform
        .find(_._1 === method)
        .map(_._2)
        .map { transformIndices =>
          args.zipWithIndex.flatMap {
            case (arg: Function0[_], idx) if transformIndices.contains(idx)   => List(arg())
            case (arg: Traversable[_], idx) if transformIndices.contains(idx) => arg
            case (arg, _)                                                     => List(arg)
          }
        }
        .getOrElse(args)

      unwrapVarargs(transformed)
    }
  }

  object ArgumentExtractor {
    val Empty = ArgumentExtractor(Seq.empty)
  }
}
