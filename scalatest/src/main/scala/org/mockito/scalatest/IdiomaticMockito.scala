package org.mockito.scalatest

import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockitoBase }
import org.scalatest.Succeeded
import org.scalatest.compatible.Assertion

trait IdiomaticMockito extends IdiomaticMockitoBase with ArgumentMatchersSugar with MockitoSessionFixture {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait AsyncIdiomaticMockito extends IdiomaticMockitoBase with ArgumentMatchersSugar with MockitoSessionAsyncFixture {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait MockitoSugar      extends org.mockito.MockitoSugar with ArgumentMatchersSugar with MockitoSessionFixture
trait AsyncMockitoSugar extends org.mockito.MockitoSugar with ArgumentMatchersSugar with MockitoSessionAsyncFixture
