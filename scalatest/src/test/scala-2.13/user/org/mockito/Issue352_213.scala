package user.org.mockito

import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.ArraySeq

class Issue352_213 extends AnyFunSpec with Matchers with MockitoSugar {

  trait IndexedSeqService {
    def testMethod(str: String, args: IndexedSeq[String]): IndexedSeq[String]
  }
  trait ArraySeqService {
    def testMethod(str: String, args: ArraySeq[String]): IndexedSeq[String]
  }

  describe("Mockito") {
    it("should allow ArraySeq as a last parameter") {
      val arraySeq = mock[ArraySeqService]
      when(arraySeq.testMethod(*, *)).thenAnswer[String, ArraySeq[String]] { (fst, rst) =>
        fst +: rst
      }
      arraySeq.testMethod("a", ArraySeq("b", "c")).should(contain).allOf("a", "b", "c")
    }
    it("should allow ArraySeq as a last parameter to IndexedSeq method") {
      val indexedSeq = mock[IndexedSeqService]
      when(indexedSeq.testMethod(*, *)).thenAnswer[String, IndexedSeq[String]] { (fst, rst) =>
        fst +: rst
      }
      indexedSeq.testMethod("a", ArraySeq("b", "c")).should(contain).allOf("a", "b", "c")
    }
  }
}
