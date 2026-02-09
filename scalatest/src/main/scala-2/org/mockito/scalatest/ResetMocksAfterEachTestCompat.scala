package org.mockito.scalatest

import org.mockito.stubbing.DefaultAnswer
import org.mockito.{ MockCreator, MockSettings }
import org.scalactic.Prettifier

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.WeakTypeTag

/**
 * Internal Scala 2 compatibility layer for `ResetMocksAfterEachTest`/`ResetMocksAfterEachAsyncTest`.
 * Provides WeakTypeTag-based mock override methods that intercept mock creation to track mocks for automatic reset.
 */
private[scalatest] trait ResetMocksAfterEachTestCompat extends MockCreator with ResetMocksAfterEachTestRuntime {

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](implicit defaultAnswer: DefaultAnswer, $pt: Prettifier): T =
    addMock(super.mock[T])

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](defaultAnswer: DefaultAnswer)(implicit $pt: Prettifier): T =
    addMock(super.mock[T](defaultAnswer))

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](mockSettings: MockSettings)(implicit $pt: Prettifier): T =
    addMock(super.mock[T](mockSettings))

  abstract override def mock[T <: AnyRef: ClassTag: WeakTypeTag](name: String)(implicit defaultAnswer: DefaultAnswer, $pt: Prettifier): T =
    addMock(super.mock[T](name))
}
