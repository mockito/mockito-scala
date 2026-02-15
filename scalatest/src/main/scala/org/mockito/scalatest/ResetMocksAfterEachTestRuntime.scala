package org.mockito.scalatest

import java.util.concurrent.ConcurrentHashMap
import scala.jdk.CollectionConverters.*

/**
 * Runtime support for ResetMocksAfterEachTestBase. Shared across Scala 2 and Scala 3.
 */
trait ResetMocksAfterEachTestRuntime {

  private val mocksToReset = ConcurrentHashMap.newKeySet[AnyRef]().asScala

  protected def resetAll(): Unit = mocksToReset.foreach(org.mockito.MockitoSugar.reset(_))

  private[scalatest] def addMock[T <: AnyRef](mock: T): T = {
    mocksToReset.add(mock)
    mock
  }

}
