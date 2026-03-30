package org.mockito.scalatest

import org.mockito.invocation.MockHandler
import org.mockito.mock.MockCreationSettings
import org.mockito.{ MockCreatorRuntime, MockSettings }
import org.scalactic.Prettifier

import java.util.concurrent.ConcurrentHashMap
import scala.jdk.CollectionConverters.*
import scala.reflect.ClassTag

/**
 * Base trait for `ResetMocksAfterEachTest`/`ResetMocksAfterEachAsyncTest`. Overrides `createMock` to intercept all mock creation for automatic post-test reset.
 */
trait ResetMocksAfterEachTestBase extends MockCreatorRuntime {

  private val mocksToReset = ConcurrentHashMap.newKeySet[AnyRef]().asScala

  protected def resetAll(): Unit = mocksToReset.foreach(org.mockito.MockitoSugar.reset(_))

  abstract override private[mockito] def createMock[T <: AnyRef: ClassTag](
      mockSettings: MockSettings,
      interfaces: List[Class[?]],
      mockHandler: (MockCreationSettings[T], Prettifier) => MockHandler[T]
  )(implicit $pt: Prettifier): T = {
    val mock = super.createMock[T](mockSettings, interfaces, mockHandler)
    mocksToReset.add(mock)
    mock
  }

}
