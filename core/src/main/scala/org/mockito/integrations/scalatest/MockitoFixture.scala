package org.mockito.integrations.scalatest

import org.mockito._

/**
 * It automatically wraps each test in a MockitoScalaSession so the implicit verifications are applied
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends WordSpec with MockitoFixture}}}
 *
 */
@deprecated("Please migrate to org.mockito.scalatest.Mockito or org.mockito.scalatest.AsyncMockito from the mockito-scala-scalatest module",
            "1.3.0")
trait MockitoFixture extends MockitoSessionFixture with MockitoSugar with ArgumentMatchersSugar
