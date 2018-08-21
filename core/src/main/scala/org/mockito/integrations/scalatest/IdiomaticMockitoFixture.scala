package org.mockito.integrations.scalatest

import org.mockito._
import org.scalatest.{ Outcome, Suite, TestSuite }

/**
 * It automatically wraps each test in a MockitoScalaSession so the implicit verifications are applied
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends WordSpec with IdiomaticMockitoFixture}}}
 *
 */
trait IdiomaticMockitoFixture extends TestSuite with IdiomaticMockito with ArgumentMatchersSugar { this: Suite =>

  abstract override def withFixture(test: NoArgTest): Outcome =
    MockitoScalaSession(name = s"MockitoFixtureSession[${test.name}]") {
      super.withFixture(test)
    }
}
