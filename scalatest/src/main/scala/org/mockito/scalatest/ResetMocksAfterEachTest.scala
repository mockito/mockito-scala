package org.mockito.scalatest

import java.util.concurrent.ConcurrentHashMap

import org.mockito.stubbing.DefaultAnswer
import org.mockito.{MockCreator, MockSettings, MockitoSugar}
import org.scalatest.{Outcome, TestSuite}

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
trait ResetMocksAfterEachTest extends TestSuite with MockCreator { self: MockCreator =>

  private val mocksToReset = ConcurrentHashMap.newKeySet[AnyRef]().asScala

  private def resetAll(): Unit = mocksToReset.foreach(MockitoSugar.reset(_))

  override protected def withFixture(test: NoArgTest): Outcome = {
    val outcome = super.withFixture(test)
    resetAll()
    outcome
  }

  private def addMock[T <: AnyRef](mock: T) = {
    mocksToReset.add(mock)
    mock
  }

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](implicit defaultAnswer: DefaultAnswer): T =
    addMock(super.mock[T])

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](defaultAnswer: DefaultAnswer): T =
    addMock(super.mock[T](defaultAnswer))

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](mockSettings: MockSettings): T =
    addMock(super.mock[T](mockSettings))

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](name: String)(implicit defaultAnswer: DefaultAnswer): T =
    addMock(super.mock[T](name))
}
