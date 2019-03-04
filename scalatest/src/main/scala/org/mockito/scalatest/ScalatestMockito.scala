package org.mockito.scalatest

import org.mockito.{ArgumentMatchersSugar, IdiomaticMockitoBase}
import org.scalatest.Succeeded
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

trait ScalatestMockito extends IdiomaticMockitoBase with ArgumentMatchersSugar with MockitoSessionFixture {
  override type Verification = Assertion
  override def verification(v: => Any): Verification = {
    v
    Succeeded
  }
}

trait ScalatestAsyncMockito extends IdiomaticMockitoBase with ArgumentMatchersSugar with MockitoSessionAsyncFixture {
  override type Verification = Future[Assertion]
  override def verification(v: => Any): Verification = {
    v
    Future.successful(Succeeded)
  }
}
