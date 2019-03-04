package org.mockito.integrations.scalatest

import org.mockito._

/**
 * It automatically wraps each test in a MockitoScalaSession so the implicit verifications are applied
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends WordSpec with IdiomaticMockitoFixture}}}
 *
 */
@deprecated("Please use org.mockito.scalatest.ScalatestMockito or org.mockito.scalatest.ScalatestAsyncMockito from the mockito-scala-scalatest module", "1.3.0")
trait IdiomaticMockitoFixture extends MockitoSessionFixture with IdiomaticMockito with ArgumentMatchersSugar