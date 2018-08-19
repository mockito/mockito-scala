package org.mockito.integrations.scalatest

import org.mockito._
import org.scalatest.{ Outcome, TestSuite }

trait MockitoFixture extends TestSuite with MockitoSugar with ArgumentMatchersSugar {

  override def withFixture(test: NoArgTest): Outcome = {
    val session = MockitoScalaSession(name = s"MockitoFixtureSession[${test.name}]")
    val outcome = super.withFixture(test)
    session.finishMocking()
    outcome
  }

}
