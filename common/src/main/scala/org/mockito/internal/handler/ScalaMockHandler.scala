package org.mockito
package internal.handler

import java.lang.reflect.Method
import java.lang.reflect.Modifier.isAbstract

import org.mockito.ReflectionUtils.methodsWithLazyOrVarArgs
import org.mockito.internal.handler.ScalaMockHandler._
import org.mockito.internal.invocation._
import org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress
import org.mockito.invocation.{ Invocation, MockHandler }
import org.mockito.mock.MockCreationSettings
import org.scalactic.Prettifier
import org.scalactic.TripleEquals._

import scala.collection.JavaConverters._

class ScalaMockHandler[T](mockSettings: MockCreationSettings[T], methodsToProcess: Seq[(Method, Set[Int])])(implicit $pt: Prettifier)
    extends MockHandlerImpl[T](mockSettings) {

  override def handle(invocation: Invocation): AnyRef = {
    val method = invocation.getMethod
    if (!isAbstract(method.getModifiers) && method.getName.contains("$default$")) invocation.callRealMethod()
    else
      super.handle {
        invocation match {
          case i: InterceptedInvocation =>
            val rawArguments = i.getRawArguments
            val arguments =
              if (rawArguments != null && rawArguments.nonEmpty && !isCallRealMethod) unwrapArgs(method, rawArguments)
              else rawArguments

            new ScalaInvocation(i.getMockRef,
                                i.getMockitoMethod,
                                arguments,
                                rawArguments,
                                i.getRealMethod,
                                i.getLocation,
                                i.getSequenceNumber)
          case other => other
        }
      }
  }

  private def unwrapArgs(method: Method, args: Array[Any]): Array[Object] = {
    val transformed = methodsToProcess
      .collectFirst {
        case (mtd, indices) if method === mtd =>
          val argumentMatcherStorage = mockingProgress().getArgumentMatcherStorage
          val matchers               = argumentMatcherStorage.pullLocalizedMatchers().asScala.toIterator
          def reportMatcher(): Unit  = if (matchers.nonEmpty) argumentMatcherStorage.reportMatcher(matchers.next().getMatcher)

          args.zipWithIndex.flatMap {
            case (arg: Function0[_], idx) if indices.contains(idx) =>
              List(arg())
            case (arg: Iterable[_], idx) if indices.contains(idx) =>
              arg.foreach(_ => reportMatcher())
              arg
            case (arg: Array[_], idx) if indices.contains(idx) =>
              arg.foreach(_ => reportMatcher())
              arg.toList
            case (arg, _) =>
              reportMatcher()
              List(arg)
          }
      }
      .getOrElse(args)

    //For some border cases, we can't extract the varargs in the nice way, so we try the brute force one
    if (args.length != transformed.length) transformed
    else unwrapVarargs(transformed)
  }
}

object ScalaMockHandler {
  implicit def anyArrayToObjectArray(a: Array[Any]): Array[Object] = a.asInstanceOf[Array[Object]]
  implicit def anyRefArrayToAnyArray(a: Array[AnyRef]): Array[Any] = a.asInstanceOf[Array[Any]]

  def apply[T](mockSettings: MockCreationSettings[T])(implicit $pt: Prettifier): MockHandler[T] =
    new InvocationNotifierHandler(
      new ScalaNullResultGuardian(
        new ScalaMockHandler(
          mockSettings,
          methodsWithLazyOrVarArgs(mockSettings.getTypeToMock +: mockSettings.getExtraInterfaces.asScala.toSeq)
        )
      ),
      mockSettings
    )

  private val InvocationClassName = classOf[ScalaInvocation].getName
  private def isCallRealMethod: Boolean =
    (new Exception).getStackTrace.toList.exists { t =>
      t.getClassName == InvocationClassName &&
      t.getMethodName == "callRealMethod"
    }
}
