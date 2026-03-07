package org.mockito.internal

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

case class UserId(value: Long) extends AnyVal

final class OrderId(val value: Long) extends AnyVal
object OrderId {
  def apply(value: Long): OrderId = new OrderId(value)
}

trait HasLongValue extends Any {
  def value: Long
}
final class TaggedId(val value: Long) extends AnyVal with HasLongValue

class ValueClassSupportTest extends AnyWordSpec with Matchers {

  "ValueClassExtractor" should {
    "extract values from case value classes" in {
      val extractor = ValueClassExtractor[UserId]
      extractor.isValueClass shouldBe true
      extractor.extract(UserId(42L)) shouldBe 42L
    }

    "extract values from non-case value classes" in {
      val extractor = ValueClassExtractor[OrderId]
      extractor.isValueClass shouldBe true
      extractor.extract(OrderId(10L)) shouldBe 10L
    }

    "treat primitives as normal types" in {
      val extractor = ValueClassExtractor[Int]
      extractor.isValueClass shouldBe false
      extractor.extract(7) shouldBe 7
    }
  }

  "ValueClassWrapper" should {
    "detect case value classes" in {
      val wrapper = ValueClassWrapper[UserId]
      wrapper.isValueClass shouldBe true
    }

    "detect non-case value classes with universal traits" in {
      val wrapper = ValueClassWrapper[TaggedId]
      wrapper.isValueClass shouldBe true
    }

    "treat primitives as normal types" in {
      val wrapper = ValueClassWrapper[Int]
      wrapper.isValueClass shouldBe false
      wrapper.wrap(7) shouldBe 7
    }

    "treat regular classes as non-value" in {
      val wrapper = ValueClassWrapper[String]
      wrapper.isValueClass shouldBe false
      wrapper.wrap("hello") shouldBe "hello"
    }
  }
}
