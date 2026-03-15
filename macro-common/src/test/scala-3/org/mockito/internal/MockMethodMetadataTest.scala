package org.mockito.internal

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.reflect.ClassTag

private case class MMT_ValueId(value: Int) extends AnyVal

private trait MMT_MetadataTarget {
  type Alias = String
  def byNameAndVarArg(x: => Int, ys: String*): Unit
  def plainFunction0(f: () => String): String
  def aliasReturn: Alias
  def valueId: MMT_ValueId
}

private trait MMT_ExtraA
private trait MMT_ExtraB
private trait MMT_ExtraC

class MockMethodMetadataTest extends AnyWordSpec with Matchers {

  "registerByNameAndVarArgInfo" should {
    "populate by-name/vararg and return metadata in cache" in {
      MockMethodMetadata.registerByNameAndVarArgInfo[MMT_MetadataTarget]

      val byNameMethod = classOf[MMT_MetadataTarget].getMethods.find(_.getName == "byNameAndVarArg").get
      val byNameInfos  = MockMetadataCache.getByName(classOf[MMT_MetadataTarget])
      byNameInfos should not be empty
      byNameInfos.get.toMap.get(byNameMethod) shouldBe Some(Set(0, 1))
      byNameInfos.get.exists(_._1.getName == "plainFunction0") shouldBe false

      val aliasMethod   = classOf[MMT_MetadataTarget].getMethod("aliasReturn")
      val valueIdMethod = classOf[MMT_MetadataTarget].getMethod("valueId")
      MockMetadataCache.getReturnType(aliasMethod) shouldBe Some(classOf[String])
      MockMetadataCache.getReturnsValueClass(valueIdMethod) shouldBe Some(true)
    }
  }

  "extraInterfacesImpl" should {
    "exclude the primary runtime class and return only extras for intersections" in {
      type Intersection = MMT_ExtraA & MMT_ExtraB & MMT_ExtraC
      val primary  = summon[ClassTag[Intersection]].runtimeClass
      val expected = Set(classOf[MMT_ExtraA], classOf[MMT_ExtraB], classOf[MMT_ExtraC]) - primary

      MockMethodMetadata.extraInterfacesImpl[Intersection].toSet shouldBe expected
    }
  }
}
