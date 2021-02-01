package user.org.mockito

import org.mockito.scalatest.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class Issue352 extends AnyFunSpec with Matchers with MockitoSugar {

  trait IndexedSeqService {
    def testMethod(str: String, args: IndexedSeq[String]): IndexedSeq[String]
  }
  trait VarArgsService {
    def testMethod(str: String, args: String*): IndexedSeq[String]
  }

  describe("Mockito") {
    it("should allow Vector as a last parameter to IndexedSeq method") {
      val indexedSeq = mock[IndexedSeqService]
      when(indexedSeq.testMethod(*, *)).thenAnswer[String, IndexedSeq[String]] { (fst, rst) =>
        fst +: rst
      }
      indexedSeq.testMethod("a", Vector("b", "c")).should(contain).allOf("a", "b", "c")
    }
    it("should allow varargs as a last parameter to IndexedSeq method 2 varargs") {
      val varargSeq = mock[VarArgsService]
      when(varargSeq.testMethod(*, *)).thenAnswer[String, String, String] { (v0, v1, v2) =>
        IndexedSeq(v0, v1, v2)
      }
      varargSeq.testMethod("a", "b", "c").should(contain).allOf("a", "b", "c")
    }
    it("should allow varargs as a last parameter to IndexedSeq method 1 vararg") {
      val varargSeq = mock[VarArgsService]
      when(varargSeq.testMethod(*, *)).thenAnswer[String, String] { (v0, v1) =>
        IndexedSeq(v0, v1)
      }
      varargSeq.testMethod("a", "b").should(contain).allOf("a", "b")
    }
    it("should allow varargs as a last parameter to IndexedSeq method no vararg") {
      val varargSeq = mock[VarArgsService]
      when(varargSeq.testMethod(*, *)).thenAnswer[String] { v0 =>
        IndexedSeq(v0)
      }
      varargSeq.testMethod("a").should(contain).only("a")
    }
  }
}
