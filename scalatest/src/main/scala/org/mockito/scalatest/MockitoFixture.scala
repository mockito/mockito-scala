package org.mockito.scalatest
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}

/**
 * It automatically wraps each test in a MockitoScalaSession so the implicit verifications are applied
 *
 * Just mix-in after your favourite suite, i.e. {{{class MyTest extends WordSpec with MockitoFixture}}}
 *
 */
trait MockitoFixture extends MockitoSessionFixture with MockitoSugar with ArgumentMatchersSugar
