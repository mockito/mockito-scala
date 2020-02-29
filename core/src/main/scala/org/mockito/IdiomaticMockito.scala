package org.mockito

trait IdiomaticMockito extends IdiomaticStubbing with PostfixVerifications {
  override type Verification = Unit
  override def verification(v: => Any): Verification = v
}

/**
 * Simple object to allow the usage of the trait without mixing it in
 */
object IdiomaticMockito extends IdiomaticMockito

// TODO need a better name
trait IdiomaticMockito2 extends IdiomaticStubbing with PrefixExpectations {
  override type Verification = Unit
  override def verification(v: => Any): Verification = v
}

/**
 * Simple object to allow the usage of the trait without mixing it in
 */
object IdiomaticMockito2 extends IdiomaticMockito2
