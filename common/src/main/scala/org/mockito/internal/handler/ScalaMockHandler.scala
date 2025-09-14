package org.mockito
package internal.handler

import java.lang.reflect.Method
import java.util.regex.Pattern
import org.mockito.ReflectionUtils.methodsWithLazyOrVarArgs
import org.mockito.internal.handler.ScalaMockHandler.*
import org.mockito.internal.invocation.*
import org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress
import org.mockito.invocation.{ Invocation, MockHandler }
import org.mockito.matchers.EqTo
import org.mockito.mock.MockCreationSettings
import org.scalactic.Prettifier
import org.scalactic.TripleEquals.*

import scala.annotation.nowarn
import scala.collection.compat.*
import scala.jdk.CollectionConverters.*

class ScalaMockHandler[T](mockSettings: MockCreationSettings[T], methodsToProcess: Seq[(Method, Set[Int])])(implicit $pt: Prettifier) extends MockHandlerImpl[T](mockSettings) {
  override def handle(invocation: Invocation): AnyRef =
    invocation match {
      case i: InterceptedInvocation =>
        val method     = i.getMethod
        val methodName = method.getName
        val realMethod = i.getRealMethod: @nowarn("cat=deprecation")
        if (realMethod.isInvokable && (methodName.contains("$default$") || ExecuteIfSpecialised(methodName)))
          i.callRealMethod()
        else {
          val rawArguments = i.getRawArguments
          val arguments    =
            if (rawArguments != null && rawArguments.nonEmpty && !isCallRealMethod) unwrapArgs(method, rawArguments)
            else rawArguments

          val scalaInvocation =
            new ScalaInvocation(i.getMockRef, i.getMockitoMethod, arguments, rawArguments, realMethod, i.getLocation, i.getSequenceNumber): @nowarn("cat=deprecation")
          super.handle(scalaInvocation)
        }
      case other => super.handle(other)
    }

  private def unwrapArgs(method: Method, args: Array[Any]): Array[Object] = {
    val transformed = methodsToProcess
      .collectFirst {
        case (mtd, indices) if method === mtd =>
          val argumentMatcherStorage                     = mockingProgress().getArgumentMatcherStorage
          val matchers                                   = argumentMatcherStorage.pullLocalizedMatchers().asScala.iterator
          val matchersWereUsed                           = matchers.nonEmpty
          def reportMatcher(): Unit                      = if (matchers.nonEmpty) argumentMatcherStorage.reportMatcher(matchers.next().getMatcher)
          def reportMatchers(varargs: Iterable[?]): Unit =
            if (matchersWereUsed && varargs.nonEmpty) {
              def reportAsEqTo(): Unit = varargs.map(EqTo(_)).foreach(argumentMatcherStorage.reportMatcher(_))
              val matcher              = matchers.next().getMatcher
              matcher match {
                case EqTo(value: Array[?]) if varargs.iterator.sameElements(value.iterator) => reportAsEqTo()
                case EqTo(value) if varargs == value                                        => reportAsEqTo()
                case other                                                                  =>
                  argumentMatcherStorage.reportMatcher(other)
                  varargs.drop(1).foreach(_ => reportMatcher())
              }
            }

          args.zipWithIndex.flatMap {
            case (arg: Function0[?], idx) if indices.contains(idx) =>
              List(arg())
            case (arg: Iterable[?], idx) if indices.contains(idx) =>
              reportMatchers(arg)
              arg
            case (arg: Array[?], idx) if indices.contains(idx) =>
              val argList = arg.toList
              reportMatchers(arg)
              argList
            case (arg, _) =>
              reportMatcher()
              List(arg)
          }
      }
      .getOrElse(args)

    // For some border cases, we can't extract the varargs in the nice way, so we try the brute force one
    if (methodsToProcess.isEmpty || args.length != transformed.length) transformed
    else unwrapVarargs(transformed)
  }
}

object ScalaMockHandler {
  private val SpecialisedMethodsPattern: Pattern      = """.*\$mc[ZBCDFIJSV]\$sp$""".r.pattern
  private val ExecuteIfSpecialised: String => Boolean = SpecialisedMethodsPattern.matcher(_).matches()

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

  private val InvocationClassName       = classOf[ScalaInvocation].getName
  private def isCallRealMethod: Boolean =
    (new Exception).getStackTrace.toList.exists { t =>
      t.getClassName == InvocationClassName &&
      t.getMethodName == "callRealMethod"
    }
}
