package org.mockito.integrations.scalatest

import org.mockito._
import org.scalatest.{ Outcome, Suite, TestSuite }

/**
 * It automatically wraps each test in a MockitoScalaSession so the implicit verifications are applied
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends WordSpec with MockitoFixture}}}
 *
 */
trait MockitoFixture extends TestSuite with MockitoSugar with ArgumentMatchersSugar { this: Suite =>

  abstract override def withFixture(test: NoArgTest): Outcome =
    MockitoScalaSession(name = s"MockitoFixtureSession[${test.name}]") {
      super.withFixture(test)
    }
}
