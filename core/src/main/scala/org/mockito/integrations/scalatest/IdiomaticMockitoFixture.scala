package org.mockito.integrations.scalatest

import org.mockito._

/**
 * It automatically wraps each test in a MockitoScalaSession so the implicit verifications are applied
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends WordSpec with IdiomaticMockitoFixture}}}
 *
 */
trait IdiomaticMockitoFixture extends MockitoSessionFixture with IdiomaticMockito with ArgumentMatchersSugar