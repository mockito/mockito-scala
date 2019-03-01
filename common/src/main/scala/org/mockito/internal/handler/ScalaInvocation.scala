package org.mockito.internal.handler
import java.lang.reflect.Method

import org.mockito.internal.exceptions.Reporter.cannotCallAbstractRealMethod
import org.mockito.internal.exceptions.VerificationAwareInvocation
import org.mockito.internal.invocation.{MockitoMethod, RealMethod}
import org.mockito.internal.invocation.mockref.MockReference
import org.mockito.invocation.{Invocation, Location, StubInfo}

class ScalaInvocation(val mockRef: MockReference[AnyRef],
                      val mockitoMethod: MockitoMethod,
                      val arguments: Array[AnyRef],
                      rawArguments: Array[AnyRef],
                      realMethod: RealMethod,
                      location: Location,
                      sequenceNumber: Int)
  extends Invocation
    with VerificationAwareInvocation {

  private var verified: Boolean                  = false
  private var _isIgnoredForVerification: Boolean = false
  private var _stubInfo: StubInfo                = _

  override def getArguments: Array[AnyRef]           = arguments
  override def getArgument[T](index: Int): T         = arguments(index).asInstanceOf[T]
  override def getSequenceNumber: Int                = sequenceNumber
  override def getLocation: Location                 = location
  override def getRawArguments: Array[AnyRef]        = rawArguments
  override def getRawReturnType: Class[_]            = mockitoMethod.getReturnType
  override def markVerified(): Unit                  = verified = true
  override def stubInfo(): StubInfo                  = _stubInfo
  override def markStubbed(stubInfo: StubInfo): Unit = _stubInfo = stubInfo
  override def isIgnoredForVerification: Boolean     = _isIgnoredForVerification
  override def ignoreForVerification(): Unit         = _isIgnoredForVerification = true
  override def isVerified: Boolean                   = verified || isIgnoredForVerification
  override def getMock: AnyRef                       = mockRef.get
  override def getMethod: Method                     = mockitoMethod.getJavaMethod
  override def callRealMethod(): AnyRef =
    if (realMethod.isInvokable) realMethod.invoke
    else throw cannotCallAbstractRealMethod

  override def equals(other: Any): Boolean = other match {
    case that: ScalaInvocation =>
      super.equals(that) &&
        getMock == that.getMock &&
        mockitoMethod == that.mockitoMethod &&
        (arguments sameElements that.arguments)
    case _ => false
  }
  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), mockRef.get, mockitoMethod, arguments)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
