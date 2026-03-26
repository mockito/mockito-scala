package org.mockito

import org.mockito.captor.Captor
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/** Fixture for Scala 3-specific macro tests. */
private trait MacroSub_Service {
  def value(): Int
  def ping(): Unit
  def transform(s: String): Int
}

private class MacroSub_ConcreteService extends MacroSub_Service {
  override def value(): Int              = 100
  override def ping(): Unit              = ()
  override def transform(s: String): Int = s.length
}

private case class MacroSub_UserId(value: Long) extends AnyVal

private trait MacroSub_UserService {
  def userId(): MacroSub_UserId
}

private trait MacroSub_Calculator {
  def add(a: Int, b: Int): Int
}

private trait MacroSub_Transformer {
  def combine(a: String, b: Int): String
}

class MacroSubScala3Test extends AnyWordSpec with Matchers {

  /** Required by VerifyMacro and ExpectMacro: wraps a verification call so the macro can locate it in the owner chain. */
  def verification(v: => Any): Unit = v

  "WhenMacro.whenRaw" should {
    "stub a plain method invocation" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      WhenMacro.whenRaw(service.value()).thenReturn(7)

      service.value() shouldBe 7
    }

    "preserve ArgumentMatcher and stub all matching calls" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      // any() is recognised as a matcher by the macro (no DefaultMatcher wrapping needed)
      WhenMacro.whenRaw(service.transform(ArgumentMatchers.any())).thenReturn(42)

      service.transform("hello") shouldBe 42
      service.transform("world") shouldBe 42
    }

    "handle named arguments with matchers in the stubbed invocation" in {
      val calc = Mockito.mock(classOf[MacroSub_Calculator])
      WhenMacro.whenRaw(calc.add(a = ArgumentMatchers.anyInt(), b = ArgumentMatchers.anyInt())).thenReturn(99)

      calc.add(a = 1, b = 2) shouldBe 99
      calc.add(a = 7, b = 8) shouldBe 99
    }
  }

  "DoSomethingMacro" should {
    "stub return values via returnedByMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      DoSomethingMacro.returnedByMacro(11, service.value())

      service.value() shouldBe 11
    }

    "stub Unit methods via doesNothing" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      DoSomethingMacro.doesNothing(service.ping())

      service.ping()
      Mockito.verify(service).ping()
    }

    "stub exception throwing via thrownByMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      DoSomethingMacro.thrownByMacro(new RuntimeException("boom"), service.value())

      val ex = intercept[RuntimeException](service.value())
      ex.getMessage shouldBe "boom"
    }

    "stub with a function via answeredByMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      // any() is a matcher → no DefaultMatcher wrapping in the template invocation
      DoSomethingMacro.answeredByMacro((_: String).length, service.transform(ArgumentMatchers.any()))

      service.transform("hello") shouldBe 5
      service.transform("hi") shouldBe 2
    }

    "stub a method returning a value class via returnedByMacro" in {
      val service = Mockito.mock(classOf[MacroSub_UserService])
      DoSomethingMacro.returnedByMacro(MacroSub_UserId(42L), service.userId())

      service.userId() shouldBe MacroSub_UserId(42L)
    }

    "stub with an argument matcher in the template via returnedByMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      DoSomethingMacro.returnedByMacro(99, service.transform(ArgumentMatchers.any()))

      service.transform("anything") shouldBe 99
      service.transform("else") shouldBe 99
    }

    "stub with a two-argument function via answeredByMacro" in {
      val t = Mockito.mock(classOf[MacroSub_Transformer])
      DoSomethingMacro.answeredByMacro((s: String, n: Int) => s * n, t.combine(ArgumentMatchers.any(), ArgumentMatchers.anyInt()))

      t.combine("ab", 3) shouldBe "ababab"
    }

    "stub with a deferred thunk via answeredByThunkMacro" in {
      val service   = Mockito.mock(classOf[MacroSub_Service])
      var callCount = 0
      DoSomethingMacro.answeredByThunkMacro(() => { callCount += 1; callCount }, service.value())

      service.value() shouldBe 1
      service.value() shouldBe 2
    }

    "route to the real implementation via calledBy" in {
      val spy = Mockito.spy(new MacroSub_ConcreteService)
      DoSomethingMacro.returnedByMacro(999, spy.value())
      spy.value() shouldBe 999

      DoSomethingMacro.calledBy(spy.value())
      spy.value() shouldBe 100
    }

    "route to the real implementation via Called.by" in {
      val spy = Mockito.spy(new MacroSub_ConcreteService)
      DoSomethingMacro.returnedByMacro(999, spy.value())
      spy.value() shouldBe 999

      Called.by(spy.value())
      spy.value() shouldBe 100
    }
  }

  "VerifyMacro" should {
    "verify a method was called once via wasMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      WhenMacro.whenRaw(service.value()).thenReturn(5)
      service.value()

      VerifyMacro.wasMacro[Int, Unit](service.value(), ())(using VerifyUnOrdered)
    }

    "verify a method was never called via wasNeverMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])

      VerifyMacro.wasNeverMacro[Int, Unit](service.value(), ())(using VerifyUnOrdered)
    }

    "verify zero interactions on a mock via wasNeverMacro on the mock itself" in {
      val service = Mockito.mock(classOf[MacroSub_Service])

      VerifyMacro.wasNeverMacro[MacroSub_Service, Unit](service, ())(using VerifyUnOrdered)
    }

    "verify a method was called once via wasCalledMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      WhenMacro.whenRaw(service.value()).thenReturn(5)
      service.value()

      VerifyMacro.wasCalledMacro[Int, Unit](service.value(), VerifyMacroRuntime.Once)(using VerifyUnOrdered)
    }

    "verify no more interactions via wasNeverCalledAgainMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      WhenMacro.whenRaw(service.value()).thenReturn(5)
      service.value()
      Mockito.verify(service).value()

      VerifyMacro.wasNeverCalledAgainMacro[MacroSub_Service, Unit](service, ())
    }

    "verify with an argument matcher via wasMacro" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      service.transform("hello")

      VerifyMacro.wasMacro[Int, Unit](service.transform(ArgumentMatchers.any()), ())(using VerifyUnOrdered)
    }

    "verify with named args via wasMacro" in {
      val calc = Mockito.mock(classOf[MacroSub_Calculator])
      calc.add(1, 2)

      VerifyMacro.wasMacro[Int, Unit](calc.add(a = ArgumentMatchers.anyInt(), b = ArgumentMatchers.anyInt()), ())(using VerifyUnOrdered)
    }
  }

  "ExpectMacro" should {
    "verify a method was called once via callsTo" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      WhenMacro.whenRaw(service.value()).thenReturn(5)
      service.value()

      ExpectMacro.callsTo[Unit](service.value(), VerifyMacroRuntime.Once)(VerifyUnOrdered)
    }

    "verify zero interactions on a mock via callsOn" in {
      val service = Mockito.mock(classOf[MacroSub_Service])

      ExpectMacro.callsOn[Unit](service)
    }

    "verify no more interactions via callsOnNoMore" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      WhenMacro.whenRaw(service.value()).thenReturn(5)
      service.value()
      Mockito.verify(service).value()

      ExpectMacro.callsOnNoMore[Unit](service, false)
    }

    "verify with an argument matcher via callsTo" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      service.transform("world")

      ExpectMacro.callsTo[Unit](service.transform(ArgumentMatchers.any()), VerifyMacroRuntime.Once)(VerifyUnOrdered)
    }

    "verify no more interactions (ignoringStubs) via callsOnNoMore" in {
      val service = Mockito.mock(classOf[MacroSub_Service])
      WhenMacro.whenRaw(service.value()).thenReturn(5)
      service.value()
      Mockito.verify(service).value()

      ExpectMacro.callsOnNoMore[Unit](service, true)
    }
  }

  "Captor" should {
    "use ValueClassCaptor for value classes" in {
      summon[Captor[MacroSub_UserId]].getClass.getSimpleName should include("ValueClassCaptor")
    }
  }
}
