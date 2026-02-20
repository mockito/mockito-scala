package org.mockito

import org.mockito.WhenMacroRuntime.RealMethod

/**
 * Runtime support for IdiomaticStubbing. Shared across Scala 2 and Scala 3. Macro-based stubbing operations are in version-specific IdiomaticStubbing.
 */
trait IdiomaticStubbingRuntime extends MockitoEnhancer with ScalacticSerialisableHack {
  import org.mockito.IdiomaticMockitoBaseRuntime.*

  val thrown: Thrown.type            = Thrown
  val returned: Returned.type        = Returned
  val answered: Answered.type        = Answered
  val theRealMethod: RealMethod.type = RealMethod

  val realMethod: RealMethod.type = RealMethod
}
