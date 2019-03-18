package org.mockito
package internal.handler

import java.lang.reflect.Method
import java.lang.reflect.Modifier.isAbstract
import java.util.concurrent.ConcurrentHashMap

import org.mockito.internal.handler.ScalaMockHandler._
import org.mockito.internal.invocation.mockref.MockReference
import org.mockito.internal.invocation.{ InterceptedInvocation, MockitoMethod, RealMethod }
import org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress
import org.mockito.internal.invocation._
import org.mockito.invocation.{ Invocation, MockHandler }
import org.mockito.mock.MockCreationSettings
import org.scalactic.Prettifier
import org.scalactic.TripleEquals._
import collection.JavaConverters._

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
              unwrapArgs(mockitoMethod.getJavaMethod, rawArguments.asInstanceOf[Array[Any]])
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

  private def unwrapArgs(method: Method, args: Array[Any]): Array[Object] = {
    val transformed = Extractors.asScala.values.flatten
      .find {
        case (cls, mtd, _) => method.getDeclaringClass.isAssignableFrom(cls) && method === mtd
      }
      .map(_._3)
      .map { transformIndices =>
        val matchers = mockingProgress().getArgumentMatcherStorage.pullLocalizedMatchers().asScala.toIterator
        val a: Array[Any] = args.zipWithIndex.flatMap {
          case (arg: Function0[_], idx) if transformIndices.contains(idx) =>
            List(arg())
          case (arg: Iterable[_], idx) if transformIndices.contains(idx) =>
            arg.foreach(_ => if (matchers.nonEmpty) mockingProgress().getArgumentMatcherStorage.reportMatcher(matchers.next().getMatcher))
            arg
          case (arg, _) =>
            if (matchers.nonEmpty) mockingProgress().getArgumentMatcherStorage.reportMatcher(matchers.next().getMatcher)
            List(arg)
        }
        a
      }
      .getOrElse(args)

    unwrapVarargs(transformed).asInstanceOf[Array[Object]]
  }

  val Extractors = new ConcurrentHashMap[Class[_], Seq[(Class[_], Method, Set[Int])]]
}
