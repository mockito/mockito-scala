package org.mockito
package internal.invocation

import java.lang.reflect.Method
import java.util

import org.mockito.internal.ValueClassExtractor
import org.mockito.internal.exceptions.Reporter.cannotCallAbstractRealMethod
import org.mockito.internal.exceptions.VerificationAwareInvocation
import org.mockito.internal.invocation.mockref.MockReference
import org.mockito.internal.reporting.PrintSettings
import org.mockito.invocation.{ Invocation, Location, StubInfo }
import org.mockito.matchers.EqTo
import org.scalactic.Prettifier

import scala.collection.JavaConverters._

class ScalaInvocation(
    val mockRef: MockReference[AnyRef],
    val mockitoMethod: MockitoMethod,
    val arguments: Array[AnyRef],
    rawArguments: Array[AnyRef],
    realMethod: RealMethod,
    location: Location,
    sequenceNumber: Int
)(implicit $pt: Prettifier)
    extends Invocation
    with VerificationAwareInvocation {
  private var verified: Boolean                  = false
  private var _isIgnoredForVerification: Boolean = false
  private var _stubInfo: StubInfo                = _

  override def getArguments: Array[AnyRef]                    = arguments
  override def getArgument[T](index: Int): T                  = arguments(index).asInstanceOf[T]
  override def getArgument[T](index: Int, clazz: Class[T]): T = clazz.cast(arguments(index))
  override def getSequenceNumber: Int                         = sequenceNumber
  override def getLocation: Location                          = location
  override def getRawArguments: Array[AnyRef]                 = rawArguments
  override def getRawReturnType: Class[_]                     = mockitoMethod.getReturnType
  override def markVerified(): Unit                           = verified = true
  override def stubInfo(): StubInfo                           = _stubInfo
  override def markStubbed(stubInfo: StubInfo): Unit          = _stubInfo = stubInfo
  override def isIgnoredForVerification: Boolean              = _isIgnoredForVerification
  override def ignoreForVerification(): Unit                  = _isIgnoredForVerification = true
  override def isVerified: Boolean                            = verified || isIgnoredForVerification
  override def getMock: AnyRef                                = mockRef.get
  override def getMethod: Method                              = mockitoMethod.getJavaMethod
  override def callRealMethod(): AnyRef =
    if (realMethod.isInvokable) realMethod.invoke
    else throw cannotCallAbstractRealMethod

  override def equals(other: Any): Boolean =
    other match {
      case that: ScalaInvocation =>
        super.equals(that) &&
        getMock == that.getMock &&
        mockitoMethod == that.mockitoMethod &&
        (arguments sameElements that.arguments)
      case _ => false
    }
  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), getMock, mockitoMethod, arguments.toSeq)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
  override def toString: String = new PrintSettings().print(getArgumentsAsMatchers, this)
  override def getArgumentsAsMatchers: util.List[ArgumentMatcher[_]] =
    arguments.map(EqTo(_)(serialisableEquality[AnyRef], ValueClassExtractor.instance[AnyRef], $pt): ArgumentMatcher[_]).toList.asJava
}
