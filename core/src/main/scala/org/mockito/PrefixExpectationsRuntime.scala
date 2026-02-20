package org.mockito

/**
 * Runtime support for PrefixExpectations. Shared across Scala 2 and Scala 3. Macro-based expectation operations are in version-specific PrefixExpectations.
 */
trait PrefixExpectationsRuntime extends IdiomaticVerifications {

  import org.mockito.IdiomaticMockitoBaseRuntime.*

  type Calls = Times

  val call: CallWord.type   = CallWord
  val calls: CallsWord.type = CallsWord

  val ignoringStubs: IgnoringStubs.type = IgnoringStubs

  implicit class IntOps(i: Int) {
    def calls: Calls = Times(i)
    def call: Calls  = Times(i)
  }
}
