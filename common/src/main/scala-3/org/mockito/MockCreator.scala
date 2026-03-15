package org.mockito

import org.mockito.internal.MockMethodMetadata
import org.mockito.invocation.MockHandler
import org.mockito.mock.MockCreationSettings
import org.mockito.quality.Strictness
import org.mockito.stubbing.{ Answer, CallsRealMethods, DefaultAnswer }
import org.scalactic.Prettifier

import scala.annotation.targetName
import scala.reflect.ClassTag

/**
 * Scala 3 mock/spy creation facade.
 *
 * Design notes:
 *   - `@targetName` is used on overloads so generated JVM method names stay unique and stable.
 *   - `inline` keeps the concrete `T` at call sites, allowing compile-time metadata extraction.
 *   - `createMock` registers per-method metadata via [[org.mockito.internal.MockMethodMetadata.registerByNameAndVarArgInfo]] before delegating to runtime creation; this feeds
 *     `ReflectionUtils`/`ScalaMockHandler` with by-name, vararg and return metadata.
 */
private[mockito] trait MockCreator extends MockCreatorRuntime {

  inline def mock[T <: AnyRef: ClassTag](using defaultAnswer: DefaultAnswer, $pt: Prettifier): T =
    mock[T](defaultAnswer)

  @targetName("mockWithAnswer")
  inline def mock[T <: AnyRef: ClassTag](defaultAnswer: Answer[?])(using $pt: Prettifier): T =
    mock[T](DefaultAnswer(defaultAnswer))

  @targetName("mockWithDefaultAnswer")
  inline def mock[T <: AnyRef: ClassTag](defaultAnswer: DefaultAnswer)(using $pt: Prettifier): T =
    mock[T](withSettings(defaultAnswer))

  @targetName("mockWithSettings")
  inline def mock[T <: AnyRef: ClassTag](mockSettings: MockSettings)(using $pt: Prettifier): T =
    createMock[T](mockSettings, (settings, pt) => org.mockito.internal.handler.ScalaMockHandler(settings)(pt))

  @targetName("mockWithName")
  inline def mock[T <: AnyRef: ClassTag](name: String)(using defaultAnswer: DefaultAnswer, $pt: Prettifier): T =
    mock[T](withSettings.name(name))

  inline def spy[T <: AnyRef: ClassTag](realObj: T, lenient: Boolean = false)(using $pt: Prettifier): T = {
    val mockSettings: MockSettings = withSettings(CallsRealMethods).spiedInstance(realObj)
    val settings                   = if (lenient) mockSettings.strictness(Strictness.LENIENT) else mockSettings
    mock[T](settings)
  }

  /**
   * Internal creation path that records metadata for `T` (compile-time) and then creates the mock (runtime).
   *
   * Registration must happen before first invocation handling so argument unwrapping and return-type classification can use cache data immediately.
   */
  private[mockito] inline def createMock[T <: AnyRef: ClassTag](
      mockSettings: MockSettings,
      mockHandler: (MockCreationSettings[T], Prettifier) => MockHandler[T]
  )(using $pt: Prettifier): T = {
    MockMethodMetadata.registerByNameAndVarArgInfo[T]
    createMock[T](mockSettings, ReflectionUtils.extraInterfaces[T], mockHandler)
  }
}
