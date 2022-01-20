package org.mockito

trait IdiomaticMockito extends IdiomaticStubbing with PostfixVerifications {
  override type Verification = Unit
  override def verification(v: => Any): Verification = v
}

object IdiomaticMockito extends IdiomaticMockito {

  /**
   * EXPERIMENTAL base trait using new prefix DSL for verifications. Use with care: API may change between minor versions.
   */
  trait WithExpect extends IdiomaticStubbing with PrefixExpectations {
    override type Verification = Unit
    override def verification(v: => Any): Verification = v
  }

  object WithExpect extends WithExpect
}
