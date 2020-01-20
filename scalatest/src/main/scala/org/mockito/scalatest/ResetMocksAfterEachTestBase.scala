package org.mockito.scalatest

import java.util.concurrent.ConcurrentHashMap

import org.mockito.stubbing.DefaultAnswer
import org.mockito.{ MockCreator, MockSettings }
import org.scalactic.Prettifier

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.WeakTypeTag

/**
 * It automatically resets each mock after a each test is run, useful when we need to pass the mocks to some framework
 * once at the beginning of the test suite
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends PlaySpec with MockitoSugar with ResetMocksAfterEachTest}}}
 *
 */
trait ResetMocksAfterEachTestBase extends MockCreator { self: MockCreator =>

  private val mocksToReset = ConcurrentHashMap.newKeySet[AnyRef]().asScala

  protected def resetAll(): Unit = mocksToReset.foreach(org.mockito.MockitoSugar.reset(_))

  private def addMock[T <: AnyRef](mock: T) = {
    mocksToReset.add(mock)
    mock
  }

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](implicit defaultAnswer: DefaultAnswer, $pt: Prettifier): T =
    addMock(super.mock[T])

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](defaultAnswer: DefaultAnswer)(implicit $pt: Prettifier): T =
    addMock(super.mock[T](defaultAnswer))

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](mockSettings: MockSettings)(implicit $pt: Prettifier): T =
    addMock(super.mock[T](mockSettings))

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](name: String)(implicit defaultAnswer: DefaultAnswer, $pt: Prettifier): T =
    addMock(super.mock[T](name))
}
