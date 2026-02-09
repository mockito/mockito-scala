package org.mockito

import org.mockito.internal.handler.ScalaMockHandler
import org.mockito.invocation.MockHandler
import org.mockito.mock.MockCreationSettings
import org.mockito.quality.Strictness
import org.mockito.stubbing.{ Answer, CallsRealMethods, DefaultAnswer }
import org.scalactic.Prettifier

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.WeakTypeTag

private[mockito] trait MockCreator extends MockCreatorRuntime {

  /**
   * Delegates to <code>Mockito.mock(type: Class[T])</code> It provides a nicer API as you can, for instance, do <code>mock[MyClass]</code> instead of
   * <code>mock(classOf[MyClass])</code>
   *
   * It also pre-stub the mock so the compiler-generated methods that provide the values for the default arguments are called, ie: given <code>def
   * iHaveSomeDefaultArguments(noDefault: String, default: String = "default value")</code>
   *
   * without this fix, if you call it as <code>iHaveSomeDefaultArguments("I'm not gonna pass the second argument")</code> then you could have not verified it like
   * <code>verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")</code> as the value for the second parameter would have been null...
   */
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](implicit defaultAnswer: DefaultAnswer, $pt: Prettifier): T =
    mock[T](defaultAnswer)

  def mock[T <: AnyRef: ClassTag: WeakTypeTag](defaultAnswer: Answer[?])(implicit $pt: Prettifier): T =
    mock[T](DefaultAnswer(defaultAnswer))

  /**
   * Delegates to <code>Mockito.mock(type: Class[T], defaultAnswer: Answer[_])</code> It provides a nicer API as you can, for instance, do <code>mock[MyClass](defaultAnswer)</code>
   * instead of <code>mock(classOf[MyClass], defaultAnswer)</code>
   *
   * It also pre-stub the mock so the compiler-generated methods that provide the values for the default arguments are called, ie: given <code>def
   * iHaveSomeDefaultArguments(noDefault: String, default: String = "default value")</code>
   *
   * without this fix, if you call it as <code>iHaveSomeDefaultArguments("I'm not gonna pass the second argument")</code> then you could have not verified it like
   * <code>verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")</code> as the value for the second parameter would have been null...
   */
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](defaultAnswer: DefaultAnswer)(implicit $pt: Prettifier): T =
    mock[T](withSettings(defaultAnswer))

  /**
   * Delegates to <code>Mockito.mock(type: Class[T], mockSettings: MockSettings)</code> It provides a nicer API as you can, for instance, do
   * <code>mock[MyClass](mockSettings)</code> instead of <code>mock(classOf[MyClass], mockSettings)</code>
   *
   * It also pre-stub the mock so the compiler-generated methods that provide the values for the default arguments are called, ie: given <code>def
   * iHaveSomeDefaultArguments(noDefault: String, default: String = "default value")</code>
   *
   * without this fix, if you call it as <code>iHaveSomeDefaultArguments("I'm not gonna pass the second argument")</code> then you could have not verified it like
   * <code>verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")</code> as the value for the second parameter would have been null...
   */
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](mockSettings: MockSettings)(implicit $pt: Prettifier): T =
    createMock(mockSettings)

  /**
   * Delegates to <code>Mockito.mock(type: Class[T], name: String)</code> It provides a nicer API as you can, for instance, do <code>mock[MyClass](name)</code> instead of
   * <code>mock(classOf[MyClass], name)</code>
   *
   * It also pre-stub the mock so the compiler-generated methods that provide the values for the default arguments are called, ie: given <code>def
   * iHaveSomeDefaultArguments(noDefault: String, default: String = "default value")</code>
   *
   * without this fix, if you call it as <code>iHaveSomeDefaultArguments("I'm not gonna pass the second argument")</code> then you could have not verified it like
   * <code>verify(aMock).iHaveSomeDefaultArguments("I'm not gonna pass the second argument", "default value")</code> as the value for the second parameter would have been null...
   */
  def mock[T <: AnyRef: ClassTag: WeakTypeTag](name: String)(implicit defaultAnswer: DefaultAnswer, $pt: Prettifier): T =
    mock(withSettings.name(name))

  def spy[T <: AnyRef: ClassTag: WeakTypeTag](realObj: T, lenient: Boolean = false)(implicit $pt: Prettifier): T = {
    val mockSettings: MockSettings = withSettings(CallsRealMethods).spiedInstance(realObj)
    val settings                   = if (lenient) mockSettings.strictness(Strictness.LENIENT) else mockSettings
    mock[T](settings)
  }

  private[mockito] def createMock[T <: AnyRef: ClassTag: WeakTypeTag](
      mockSettings: MockSettings,
      mockHandler: (MockCreationSettings[T], Prettifier) => MockHandler[T] = (settings: MockCreationSettings[T], pt: Prettifier) => ScalaMockHandler(settings)(pt)
  )(implicit $pt: Prettifier): T =
    createMock[T](mockSettings, ReflectionUtils.extraInterfaces[T], mockHandler)
}
