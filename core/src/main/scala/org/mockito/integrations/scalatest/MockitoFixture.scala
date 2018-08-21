package org.mockito.integrations.scalatest

import org.mockito._
import org.scalatest.{ Outcome, Suite, TestSuite }

trait MockitoFixture extends TestSuite with MockitoSugar with ArgumentMatchersSugar { this: Suite =>

  abstract override def withFixture(test: NoArgTest): Outcome =
    MockitoScalaSession(name = s"MockitoFixtureSession[${test.name}]") {
      super.withFixture(test)
    }
}
