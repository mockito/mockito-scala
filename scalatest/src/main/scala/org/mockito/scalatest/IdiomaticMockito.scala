package org.mockito.scalatest

import org.mockito.PostfixVerifications
import org.mockito.ArgumentMatchersSugar
import org.mockito.IdiomaticVerifications
import org.scalatest.Succeeded
import org.scalatest.compatible.Assertion

trait IdiomaticMockitoBase extends org.mockito.IdiomaticStubbing with ArgumentMatchersSugar with MockitoSessionFixture { this: IdiomaticVerifications =>
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait IdiomaticMockito extends IdiomaticMockitoBase with PostfixVerifications {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait AsyncIdiomaticMockitoBase extends org.mockito.IdiomaticStubbing with ArgumentMatchersSugar with MockitoSessionAsyncFixture { this: IdiomaticVerifications =>
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait AsyncIdiomaticMockito extends AsyncIdiomaticMockitoBase with PostfixVerifications {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait MockitoSugar      extends org.mockito.MockitoSugar with ArgumentMatchersSugar with MockitoSessionFixture
trait AsyncMockitoSugar extends org.mockito.MockitoSugar with ArgumentMatchersSugar with MockitoSessionAsyncFixture
