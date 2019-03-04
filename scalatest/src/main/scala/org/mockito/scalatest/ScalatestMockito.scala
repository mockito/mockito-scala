package org.mockito.scalatest

import org.mockito.{ArgumentMatchersSugar, IdiomaticMockitoBase}
import org.scalatest.Succeeded
import org.scalatest.compatible.Assertion

trait ScalatestMockito extends IdiomaticMockitoBase with ArgumentMatchersSugar {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}
