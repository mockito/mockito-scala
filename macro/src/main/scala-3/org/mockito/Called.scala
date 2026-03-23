package org.mockito

/**
 * Stub so that the shared WhenMacroRuntime (scala/) compiles under Scala 3. WhenMacroRuntime references Called.type in RealMethod.willBe. The full implementation with the by[T]
 * macro will be added in the upcoming commits.
 */
object Called
