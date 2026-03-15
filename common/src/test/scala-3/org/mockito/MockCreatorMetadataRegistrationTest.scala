package org.mockito

import org.mockito.internal.MockMetadataCache
import org.scalactic.Prettifier
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

private[mockito] case class MCR_ValueId(value: Int) extends AnyVal

private[mockito] trait MCR_MetadataTarget {
  type Alias = String
  def byNameAndVarArg(x: => Int, ys: String*): Unit
  def plainFunction0(f: () => String): String
  def aliasReturn: Alias
  def valueId: MCR_ValueId
}

class MockCreatorMetadataRegistrationTest extends AnyWordSpec with Matchers {
  private object TestCreator extends MockCreator

  implicit private val prettifier: Prettifier = Prettifier.default

  "MockCreator.createMock" should {
    "register metadata via MockMethodMetadata before creating the mock" in {
      val mocked = TestCreator.mock[MCR_MetadataTarget](DefaultAnswers.ReturnsDefaults)
      mocked should not be null

      val byNameMethod = classOf[MCR_MetadataTarget].getMethods.find(_.getName == "byNameAndVarArg").get
      val byNameInfos  = MockMetadataCache.getByName(classOf[MCR_MetadataTarget])
      byNameInfos should not be empty
      byNameInfos.get.toMap.get(byNameMethod) shouldBe Some(Set(0, 1))
      byNameInfos.get.exists(_._1.getName == "plainFunction0") shouldBe false

      val aliasMethod   = classOf[MCR_MetadataTarget].getMethod("aliasReturn")
      val valueIdMethod = classOf[MCR_MetadataTarget].getMethod("valueId")
      MockMetadataCache.getReturnType(aliasMethod) shouldBe Some(classOf[String])
      MockMetadataCache.getReturnsValueClass(valueIdMethod) shouldBe Some(true)
    }
  }
}
