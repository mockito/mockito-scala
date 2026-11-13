package org.mockito

// Explicit Unit results keep JVM signatures stable across Scala 3.3 and 3.9's different erasure of Unit aliases.
trait IdiomaticMockito extends IdiomaticStubbing with PostfixVerifications {
  override type Verification = Unit
  override def verification(v: => Any): Unit = v
}

object IdiomaticMockito extends IdiomaticMockito {

  /**
   * EXPERIMENTAL base trait using new prefix DSL for verifications. Use with care: API may change between minor versions.
   */
  trait WithExpect extends IdiomaticStubbing with PrefixExpectations {
    override type Verification = Unit
    override def verification(v: => Any): Unit = v
  }

  object WithExpect extends WithExpect
}
