package org.mockito.integrations.scalatest

import java.util.concurrent.ConcurrentHashMap

import org.mockito.stubbing.Answer
import org.mockito.{MockCreator, MockSettings, MockitoSugar}
import org.scalatest.{Outcome, TestSuite}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

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

  abstract override def mock[T <: AnyRef: ClassTag: TypeTag]: T = addMock(super.mock[T])

  abstract override def mock[T <: AnyRef: ClassTag: TypeTag](defaultAnswer: Answer[_]): T =
    addMock(super.mock[T](defaultAnswer))

  abstract override def mock[T <: AnyRef: ClassTag: TypeTag](mockSettings: MockSettings): T =
    addMock(super.mock[T](mockSettings))

  abstract override def mock[T <: AnyRef: ClassTag: TypeTag](name: String): T = addMock(super.mock[T](name))
}
