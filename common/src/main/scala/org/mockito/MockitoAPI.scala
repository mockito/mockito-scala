/*
 * Copyright © 2017 Morgan Stanley.  All rights reserved.
 *
 * THIS SOFTWARE IS SUBJECT TO THE TERMS OF THE MIT LICENSE.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * IN ADDITION, THE FOLLOWING DISCLAIMER APPLIES IN CONNECTION WITH THIS SOFTWARE:
 * THIS SOFTWARE IS LICENSED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE AND ANY WARRANTY OF NON-INFRINGEMENT, ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. THIS SOFTWARE MAY BE REDISTRIBUTED TO OTHERS ONLY BY EFFECTIVELY USING THIS OR ANOTHER EQUIVALENT DISCLAIMER IN ADDITION TO ANY OTHER REQUIRED LICENSE TERMS.
 */

package org.mockito

import org.mockito.internal.ValueClassExtractor
import org.mockito.internal.configuration.plugins.Plugins.getMockMaker
import org.mockito.internal.creation.MockSettingsImpl
import org.mockito.internal.handler.ScalaMockHandler
import org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress
import org.mockito.internal.util.reflection.LenientCopyTool
import org.mockito.invocation.InvocationOnMock
import org.mockito.mock.MockCreationSettings
import org.mockito.stubbing.{Answer, DefaultAnswer, ScalaFirstStubbing, Stubber}
import org.mockito.verification.{VerificationMode, VerificationWithTimeout}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.WeakTypeTag

private[mockito] trait MockCreator {
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](implicit defaultAnswer: DefaultAnswer): T
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](defaultAnswer: Answer[_]): T = mock[T](DefaultAnswer(defaultAnswer))
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](defaultAnswer: DefaultAnswer): T
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](mockSettings: MockSettings): T
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](name: String)(implicit defaultAnswer: DefaultAnswer): T

  def spy[T <: AnyRef: ClassTag: WeakTypeTag](realObj: T): T
  def spyLambda[T <: AnyRef: ClassTag](realObj: T): T

  /**
   * Delegates to <code>Mockito.withSettings()</code>, it's only here to expose the full Mockito API
   */
  def withSettings(implicit defaultAnswer: DefaultAnswer): MockSettings =
    Mockito.withSettings().defaultAnswer(defaultAnswer)

}

//noinspection MutatorLikeMethodIsParameterless
private[mockito] trait DoSomething {

  /**
   * Delegates the call to <code>Mockito.doReturn(toBeReturned, toBeReturnedNext)</code>
   * but fixes the following compiler issue that happens because the overloaded vararg on the Java side
   *
   * {{{Error:(33, 25) ambiguous reference to overloaded definition,
   * both method doReturn in class Mockito of type (x$1: Any, x$2: Object*)org.mockito.stubbing.Stubber
   * and  method doReturn in class Mockito of type (x$1: Any)org.mockito.stubbing.Stubber
   * match argument types (`Type`)}}}
   *
   */
  def doReturn[T](toBeReturned: T, toBeReturnedNext: T*)(implicit $vce: ValueClassExtractor[T]): Stubber =
    Mockito.doReturn(
      $vce.extract(toBeReturned),
      toBeReturnedNext.map($vce.extract).map(_.asInstanceOf[Object]): _*
    )

  /**
   * Delegates to <code>Mockito.doThrow</code>, it's only here so we expose all the Mockito API
   * on a single place
   */
  def doThrow(toBeThrown: Throwable*): Stubber = Mockito.doThrow(toBeThrown: _*)

  /**
   * Delegates to <code>Mockito.doThrow(type: Class[T])</code>
   * It provides a nicer API as you can, for instance, do doThrow[Throwable] instead of doThrow(classOf[Throwable])
   *
   */
  def doThrow[T <: Throwable: ClassTag]: Stubber = Mockito.doThrow(clazz)

  /**
   * Delegates to <code>Mockito.doNothing()</code>, it removes the parenthesis to have a cleaner API
   */
  def doNothing: Stubber = Mockito.doNothing()

  /**
   * Delegates to <code>Mockito.doCallRealMethod()</code>, it removes the parenthesis to have a cleaner API
   */
  def doCallRealMethod: Stubber = Mockito.doCallRealMethod()

  /**
   * Delegates to <code>Mockito.doAnswer()</code>, it's only here to expose the full Mockito API
   */
  def doAnswer[R: ValueClassExtractor](l: => R): Stubber =
    Mockito.doAnswer(invocationToAnswer(_ =>
      l match {
        case f: Function0[_] => f()
        case _               => l
    }))
  def doAnswer[P0: ClassTag, R: ValueClassExtractor](f: P0 => R): Stubber = clazz[P0] match {
    case c if c == classOf[InvocationOnMock] => Mockito.doAnswer(invocationToAnswer(i => f(i.asInstanceOf[P0])))
    case _                                   => Mockito.doAnswer(functionToAnswer(f))
  }
  def doAnswer[P0, P1, R: ValueClassExtractor](f: (P0, P1) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, R: ValueClassExtractor](f: (P0, P1, P2) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, P3, R: ValueClassExtractor](f: (P0, P1, P2, P3) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, P3, P4, R: ValueClassExtractor](f: (P0, P1, P2, P3, P4) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, P3, P4, P5, R: ValueClassExtractor](f: (P0, P1, P2, P3, P4, P5) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, P3, P4, P5, P6, R: ValueClassExtractor](f: (P0, P1, P2, P3, P4, P5, P6) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, P3, P4, P5, P6, P7, R: ValueClassExtractor](f: (P0, P1, P2, P3, P4, P5, P6, P7) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, R: ValueClassExtractor](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, R: ValueClassExtractor](f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
  def doAnswer[P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R: ValueClassExtractor](
      f: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) => R): Stubber =
    Mockito.doAnswer(functionToAnswer(f))
}

private[mockito] trait MockitoEnhancer extends MockCreator {

  /**
   * Delegates to <code>Mockito.mock(type: Class[T])</code>
   * It provides a nicer API as you can, for instance, do <code>mock[MyClass]</code>
   * instead of <code>mock(classOf[MyClass])</code>
   *
   * It also pre-stub the mock so the compiler-generated methods that provide the values for the default arguments
   * are called, ie:
   * given <code>def iHaveSomeDefaultArguments(noDefault: String, default: String = "default value")</code>
   *
   * without this fix, if you call it as <code>iHaveSomeDefaultArguments("I'm not gonna pass the second argument")</code>
   * then you could have not verified it like
   * <code>verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")</code>
   * as the value for the second parameter would have been null...
   */
  override def mock[T <: AnyRef: ClassTag: WeakTypeTag](implicit defaultAnswer: DefaultAnswer): T = mock(withSettings)

  /**
   * Delegates to <code>Mockito.mock(type: Class[T], defaultAnswer: Answer[_])</code>
   * It provides a nicer API as you can, for instance, do <code>mock[MyClass](defaultAnswer)</code>
   * instead of <code>mock(classOf[MyClass], defaultAnswer)</code>
   *
   * It also pre-stub the mock so the compiler-generated methods that provide the values for the default arguments
   * are called, ie:
   * given <code>def iHaveSomeDefaultArguments(noDefault: String, default: String = "default value")</code>
   *
   * without this fix, if you call it as <code>iHaveSomeDefaultArguments("I'm not gonna pass the second argument")</code>
   * then you could have not verified it like
   * <code>verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")</code>
   * as the value for the second parameter would have been null...
   */
  override def mock[T <: AnyRef: ClassTag: WeakTypeTag](defaultAnswer: DefaultAnswer): T =
    mock(withSettings(defaultAnswer))

  /**
   * Delegates to <code>Mockito.mock(type: Class[T], mockSettings: MockSettings)</code>
   * It provides a nicer API as you can, for instance, do <code>mock[MyClass](mockSettings)</code>
   * instead of <code>mock(classOf[MyClass], mockSettings)</code>
   *
   * It also pre-stub the mock so the compiler-generated methods that provide the values for the default arguments
   * are called, ie:
   * given <code>def iHaveSomeDefaultArguments(noDefault: String, default: String = "default value")</code>
   *
   * without this fix, if you call it as <code>iHaveSomeDefaultArguments("I'm not gonna pass the second argument")</code>
   * then you could have not verified it like
   * <code>verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")</code>
   * as the value for the second parameter would have been null...
   */
  override def mock[T <: AnyRef: ClassTag: WeakTypeTag](mockSettings: MockSettings): T = {
    val interfaces = ReflectionUtils.interfaces

    val realClass: Class[T] = mockSettings match {
      case m: MockSettingsImpl[_] if !m.getExtraInterfaces.isEmpty =>
        throw new IllegalArgumentException("If you want to add extra traits to the mock use the syntax mock[MyClass with MyTrait]")
      case m: MockSettingsImpl[_] if m.getSpiedInstance != null => m.getSpiedInstance.getClass.asInstanceOf[Class[T]]
      case _                                                    => clazz
    }

    val settings =
      if (interfaces.nonEmpty) mockSettings.extraInterfaces(interfaces: _*)
      else mockSettings

    ReflectionUtils.markMethodsWithLazyArgs(realClass)

    def createMock(settings: MockCreationSettings[T]): T = {
      val mock          = getMockMaker.createMock(settings, ScalaMockHandler(settings))
      val spiedInstance = settings.getSpiedInstance
      if (spiedInstance != null) new LenientCopyTool().copyToMock(spiedInstance, mock)
      mock
    }

    settings match {
      case s: MockSettingsImpl[_] =>
        val creationSettings = s.build[T](realClass)
        val mock             = createMock(creationSettings)
        mockingProgress.mockingStarted(mock, creationSettings)
        mock
      case _ =>
        throw new IllegalArgumentException(s"""Unexpected implementation of '${settings.getClass.getCanonicalName}'
             |At the moment, you cannot provide your own implementations of that class.""".stripMargin)
    }
  }

  /**
   * Delegates to <code>Mockito.mock(type: Class[T], name: String)</code>
   * It provides a nicer API as you can, for instance, do <code>mock[MyClass](name)</code>
   * instead of <code>mock(classOf[MyClass], name)</code>
   *
   * It also pre-stub the mock so the compiler-generated methods that provide the values for the default arguments
   * are called, ie:
   * given <code>def iHaveSomeDefaultArguments(noDefault: String, default: String = "default value")</code>
   *
   * without this fix, if you call it as <code>iHaveSomeDefaultArguments("I'm not gonna pass the second argument")</code>
   * then you could have not verified it like
   * <code>verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")</code>
   * as the value for the second parameter would have been null...
   */
  override def mock[T <: AnyRef: ClassTag: WeakTypeTag](name: String)(implicit defaultAnswer: DefaultAnswer): T =
    mock(withSettings.name(name))

  def spy[T <: AnyRef: ClassTag: WeakTypeTag](realObj: T): T =
    mock[T](withSettings(DefaultAnswers.CallsRealMethods).spiedInstance(realObj))

  /**
   * Delegates to <code>Mockito.reset(T... mocks)</code>, but restores the default stubs that
   * deal with default argument values
   */
  def reset(mocks: AnyRef*): Unit = {
    val mp = mockingProgress()
    mp.validateState()
    mp.reset()
    mp.resetOngoingStubbing()

    mocks.foreach { m =>
      val oldHandler = mockingDetails(m).getMockHandler
      val settings   = oldHandler.getMockSettings
      val newHandler = ScalaMockHandler(settings)

      getMockMaker.resetMock(m, newHandler, settings)
    }
  }

  /**
   * Delegates to <code>Mockito.mockingDetails()</code>, it's only here to expose the full Mockito API
   */
  def mockingDetails(toInspect: AnyRef): MockingDetails = Mockito.mockingDetails(toInspect)

  /**
   * Delegates to <code>Mockito.verifyNoMoreInteractions(Object... mocks)</code>, but ignores the default stubs that
   * deal with default argument values
   */
  def verifyNoMoreInteractions(mocks: AnyRef*): Unit = {

    mocks.foreach { m =>
      mockingDetails(m).getInvocations.asScala
        .filter(_.getMethod.getName.contains("$default$"))
        .foreach(_.ignoreForVerification())
    }

    Mockito.verifyNoMoreInteractions(mocks: _*)
  }
}

private[mockito] trait Verifications {

  /**
   * Delegates to <code>Mockito.atLeastOnce()</code>, it removes the parenthesis to have a cleaner API
   */
  def atLeastOnce: VerificationMode = Mockito.atLeastOnce()

  /**
   * Delegates to <code>Mockito.never()</code>, it removes the parenthesis to have a cleaner API
   */
  def never: VerificationMode = Mockito.never()

  /**
   * Delegates to <code>Mockito.only()</code>, it removes the parenthesis to have a cleaner API
   */
  def only: VerificationMode = Mockito.only()

  /**
   * Delegates to <code>Mockito.timeout()</code>, it's only here to expose the full Mockito API
   */
  def timeout(millis: Int): VerificationWithTimeout = Mockito.timeout(millis)

  /**
   * Delegates to <code>Mockito.times()</code>, it's only here to expose the full Mockito API
   */
  def times(wantedNumberOfInvocations: Int): VerificationMode = Mockito.times(wantedNumberOfInvocations)

  /**
   * Delegates to <code>Mockito.calls()</code>, it's only here to expose the full Mockito API
   */
  def calls(wantedNumberOfInvocations: Int): VerificationMode = Mockito.calls(wantedNumberOfInvocations)

  /**
   * Delegates to <code>Mockito.atMost()</code>, it's only here to expose the full Mockito API
   */
  def atMost(maxNumberOfInvocations: Int): VerificationMode = Mockito.atMost(maxNumberOfInvocations)

  /**
   * Delegates to <code>Mockito.atLeast()</code>, it's only here to expose the full Mockito API
   */
  def atLeast(minNumberOfInvocations: Int): VerificationMode = Mockito.atLeast(minNumberOfInvocations)
}

/**
 * Trait that provides some basic syntax sugar.
 *
 * The idea is based on org.scalatest.mockito.MockitoSugar but it adds 100% of the Mockito API
 *
 * It also solve problems like overloaded varargs calls to Java code and pre-stub the mocks so the default arguments
 * in the method parameters work as expected
 *
 * @author Bruno Bonanno
 */
private[mockito] trait Rest extends MockitoEnhancer with DoSomething with Verifications {

  /**
   * Creates a "spy" in a way that supports lambdas and anonymous classes as they don't work with the standard spy as
   * they are created as final classes by the compiler
   */
  def spyLambda[T <: AnyRef: ClassTag](realObj: T): T = Mockito.mock(clazz, AdditionalAnswers.delegatesTo(realObj))

  /**
   * Delegates to <code>Mockito.when()</code>, it's only here to expose the full Mockito API
   */
  def when[T: ValueClassExtractor](methodCall: T): ScalaFirstStubbing[T] = Mockito.when(methodCall)

  /**
   * Delegates to <code>Mockito.ignoreStubs()</code>, it's only here to expose the full Mockito API
   */
  def ignoreStubs(mocks: AnyRef*): Array[AnyRef] = Mockito.ignoreStubs(mocks: _*)

  /**
   * Delegates to <code>Mockito.validateMockitoUsage()</code>, it's only here to expose the full Mockito API
   */
  def validateMockitoUsage(): Unit = Mockito.validateMockitoUsage()

  /**
   * Delegates to <code>Mockito.verifyZeroInteractions()</code>, it's only here to expose the full Mockito API
   */
  def verifyZeroInteractions(mocks: AnyRef*): Unit = Mockito.verifyZeroInteractions(mocks: _*)

  /**
   * Delegates to <code>Mockito.inOrder()</code>, it's only here to expose the full Mockito API
   */
  def inOrder(mocks: AnyRef*): InOrder = Mockito.inOrder(mocks: _*)

  /**
   * Delegates to <code>Mockito.verify()</code>, it's only here to expose the full Mockito API
   */
  def verify[T](mock: T): T = Mockito.verify(mock)

  /**
   * Delegates to <code>Mockito.verify()</code>, it's only here to expose the full Mockito API
   */
  def verify[T](mock: T, mode: VerificationMode): T = Mockito.verify(mock, mode)

}
