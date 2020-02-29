package org.mockito.scalatest

import org.mockito.IdiomaticStubbing
import org.mockito.PostfixVerifications
import org.mockito.PrefixExpectations
import org.mockito.ArgumentMatchersSugar
import org.scalatest.Succeeded
import org.scalatest.compatible.Assertion

trait IdiomaticMockito extends IdiomaticStubbing with PostfixVerifications with ArgumentMatchersSugar with MockitoSessionFixture {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait IdiomaticMockito2 extends IdiomaticStubbing with PrefixExpectations with ArgumentMatchersSugar with MockitoSessionFixture {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait AsyncIdiomaticMockito extends IdiomaticStubbing with PostfixVerifications with ArgumentMatchersSugar with MockitoSessionAsyncFixture {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait AsyncIdiomaticMockito2 extends IdiomaticStubbing with PrefixExpectations with ArgumentMatchersSugar with MockitoSessionAsyncFixture {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait MockitoSugar      extends org.mockito.MockitoSugar with ArgumentMatchersSugar with MockitoSessionFixture
trait AsyncMockitoSugar extends org.mockito.MockitoSugar with ArgumentMatchersSugar with MockitoSessionAsyncFixture
