package org.mockito.scalatest
import org.mockito.{ArgumentMatchersSugar, IdiomaticMockito}

/**
 * It automatically wraps each test in a MockitoScalaSession so the implicit verifications are applied
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends WordSpec with IdiomaticMockitoFixture}}}
 *
 */
trait IdiomaticMockitoFixture extends MockitoSessionFixture with IdiomaticMockito with ArgumentMatchersSugar
