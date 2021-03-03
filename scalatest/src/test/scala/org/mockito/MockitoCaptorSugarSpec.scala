package org.mockito

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
  * Copyright (C) 03.03.21 - REstore NV
  */
trait MockitoCaptorSugarTestClass {
  def fun1(int: Int): Unit
  def fun2(int: Int, string: String): Unit
}
class MockitoCaptorSugarSpec extends AnyWordSpec with MockitoSugar with MockitoCaptorSugar with Matchers {
  "capture" should {
    "capture the latest argument from a method call for 1 argument" in {
      val aMock = mock[MockitoCaptorSugarTestClass]
      aMock.fun1(1)
      aMock.fun1(2)
      capture(aMock).last(x => x.fun1 _) shouldBe 2
    }

    "capture the latest argument from a method call for 2 arguments" in {
      val aMock = mock[MockitoCaptorSugarTestClass]
      aMock.fun2(1, "hello1")
      aMock.fun2(2, "hello2")
      capture(aMock).last(x => x.fun2 _) shouldBe (2, "hello2")
    }

    "capture all arguments from a method call for 1 argument" in {
      val aMock = mock[MockitoCaptorSugarTestClass]
      aMock.fun1(1)
      aMock.fun1(2)
      capture(aMock).all(x => x.fun1 _) shouldBe Seq(1, 2)
    }

    "capture all arguments from a method call for 2 arguments" in {
      val aMock = mock[MockitoCaptorSugarTestClass]
      aMock.fun2(1, "hello1")
      aMock.fun2(2, "hello2")
      capture(aMock).last(x => x.fun2 _) shouldBe Seq((1, "hello1"), (2, "hello2"))
    }
  }
}
