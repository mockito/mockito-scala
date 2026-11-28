package org.mockito

import org.scalactic.Prettifier
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

private[mockito] case class IA_Wrapped(raw: String)

private[mockito] trait IA_Scala3Foo {
  def wrapped: IA_Wrapped
  def boxed: IA_Box[String]
}

/**
 * Scala 3 compiler-compatibility tests for [[IdiomaticStubbing]] (compiled across Scala 3.3 LTS and newer Scala 3 series against the 3.3-published artifact).
 */
class IdiomaticApiScala3Test extends AnyWordSpec with Matchers with IdiomaticMockito with ArgumentMatchersSugar {

  implicit val prettifier: Prettifier = Prettifier.default

  "IdiomaticStubbing on Scala 3" should {

    "apply given Conversion instances across heterogeneous, direct, and null arguments" in {
      given Conversion[String, IA_Wrapped]        = s => IA_Wrapped(s"s:$s")
      given Conversion[Int, IA_Wrapped]           = i => IA_Wrapped(s"i:$i")
      given [A]: Conversion[Option[A], IA_Box[A]] = opt => {
        require(opt ne null, "null must not be passed into conversion")
        IA_Box(opt)
      }

      val m1 = mock[IA_Scala3Foo]
      m1.wrapped.shouldReturn("first", 42, IA_Wrapped("direct"))
      m1.wrapped shouldBe IA_Wrapped("s:first")
      m1.wrapped shouldBe IA_Wrapped("i:42")
      m1.wrapped shouldBe IA_Wrapped("direct")

      val m2 = mock[IA_Scala3Foo]
      m2.boxed.shouldReturn(null, Some("converted"), IA_Box(Some("direct")), None)
      m2.boxed shouldBe null
      m2.boxed shouldBe IA_Box(Some("converted"))
      m2.boxed shouldBe IA_Box(Some("direct"))
      m2.boxed shouldBe IA_Box(None)
    }

    "prefer a given Conversion from Any over the fallback runtime cast" in {
      given Conversion[Any, IA_Wrapped] = a => IA_Wrapped(s"any:$a")

      val raw: Any = 99
      val m        = mock[IA_Scala3Foo]
      m.wrapped shouldReturn raw

      m.wrapped shouldBe IA_Wrapped("any:99")
    }

    "accept conforming sequence splices and reject element-converted splices" in {
      given Conversion[String, IA_Wrapped] = s => IA_Wrapped(s"s:$s")
      given Conversion[Int, IA_Wrapped]    = i => IA_Wrapped(s"i:$i")

      val m              = mock[IA_Scala3Foo]
      val conformingTail = Seq(IA_Wrapped("second"), IA_Wrapped("third"))
      m.wrapped.shouldReturn("first", conformingTail*)

      m.wrapped shouldBe IA_Wrapped("s:first")
      m.wrapped shouldBe IA_Wrapped("second")
      m.wrapped shouldBe IA_Wrapped("third")

      "m.wrapped.shouldReturn(\"first\", Seq(\"second\")*)" shouldNot typeCheck
      "m.wrapped.shouldReturn(\"first\", Seq(1, 2)*)" shouldNot typeCheck
    }

    "preserve left-to-right interleaving of argument evaluation and conversion" in {
      val events                           = scala.collection.mutable.ArrayBuffer.empty[String]
      given Conversion[String, IA_Wrapped] = s => {
        events += s"conv:$s"
        IA_Wrapped(s)
      }

      val m = mock[IA_Scala3Foo]
      m.wrapped.shouldReturn(
        { events += "eval:1"; "one" },
        { events += "eval:2"; "two" },
        { events += "eval:3"; "three" }
      )

      events.toList shouldBe List("eval:1", "conv:one", "eval:2", "conv:two", "eval:3", "conv:three")
    }

    "reject ambiguous given Conversion instances even when a local Function1 fallback exists" in {
      trait LowPriorityConversions {
        given c1: Conversion[String, IA_Wrapped] = s => IA_Wrapped(s"1:$s")
        given c2: Conversion[String, IA_Wrapped] = s => IA_Wrapped(s"2:$s")
      }
      new LowPriorityConversions {
        given localFallback: (String => IA_Wrapped) = s => IA_Wrapped(s"fn:$s")
        val m                                       = mock[IA_Scala3Foo]
        "m.wrapped shouldReturn \"ambiguous\"" shouldNot typeCheck
      }
    }
  }
}
