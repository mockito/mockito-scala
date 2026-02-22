package org.mockito

trait IdiomaticVerifications {

  type Verification

  def verification(v: => Any): Verification

}
