package org.mockito

trait IdiomaticMockito extends IdiomaticMockitoBase {
  override type Verification = Unit
  override def verification(v: => Any): Verification = v
}

/**
 * Simple object to allow the usage of the trait without mixing it in
 */
object IdiomaticMockito extends IdiomaticMockito
