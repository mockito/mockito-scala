package org.mockito

import org.mockito.internal.MockMetadataCache
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// ── test models (package-level to avoid path-dependent type issues with macro) ──

private[mockito] case class RUT_IntId(value: Int)     extends AnyVal
private[mockito] case class RUT_UserId(value: String) extends AnyVal

private[mockito] trait RUT_WithPrimitiveBacked           { def id: RUT_IntId                       }
private[mockito] trait RUT_WithReferenceBacked           { def id: RUT_UserId                      }
private[mockito] trait RUT_WithGenericBound[T <: AnyVal] { def id(v: T): T                         }
private[mockito] trait RUT_WithTypeMemberBound           { type Id <: AnyVal; def id: Id           }
private[mockito] trait RUT_WithStringTypeAlias           { type Alias = String; def getName: Alias }
private[mockito] trait RUT_WithStringReturn              { def getName: String                     }
private[mockito] trait RUT_WithByNameAndVarArg           { def foo(x: => Int, ys: String*): Unit   }
private[mockito] trait RUT_WithByNameAndFunction0        {
  def byNameArg(x: => String): String
  // Deliberately present to prove metadata marks only by-name params, not plain Function0 params.
  def function0Arg(f: () => String): String
}

/**
 * Scala 3 tests for [[MockMetadataCache]] and [[ReflectionUtils]].
 *
 * Tests register metadata explicitly in [[MockMetadataCache]], then assert cache entries and [[ReflectionUtils]] behavior.
 *
 * NOTE: plain ScalaTest assertions — NOT property-based tests.
 */
class ReflectionUtilsTest extends AnyWordSpec with Matchers {
  private val primitiveMethod          = classOf[RUT_WithPrimitiveBacked].getMethod("id")
  private val referenceMethod          = classOf[RUT_WithReferenceBacked].getMethod("id")
  private val typeMemberMethod         = classOf[RUT_WithTypeMemberBound].getMethod("id")
  private val genericBoundMethod       = classOf[RUT_WithGenericBound[RUT_IntId]].getMethod("id", classOf[Object])
  private val stringAliasMethod        = classOf[RUT_WithStringTypeAlias].getMethod("getName")
  private val stringReturnMethod       = classOf[RUT_WithStringReturn].getMethod("getName")
  private val byNameAndVarArgMethod    = classOf[RUT_WithByNameAndVarArg].getMethods.find(_.getName == "foo").get
  private val byNameAndFunction0Method = classOf[RUT_WithByNameAndFunction0].getMethod("byNameArg", classOf[scala.Function0[?]])

  MockMetadataCache.registerReturnsValueClass(
    Seq(
      primitiveMethod    -> true,
      referenceMethod    -> true,
      typeMemberMethod   -> false,
      genericBoundMethod -> false
    )
  )
  MockMetadataCache.registerReturnType(
    Seq(
      stringAliasMethod  -> classOf[String],
      stringReturnMethod -> classOf[String]
    )
  )
  MockMetadataCache.registerByName(
    classOf[RUT_WithByNameAndVarArg],
    Seq(byNameAndVarArgMethod -> Set(0, 1))
  )
  MockMetadataCache.registerByName(
    classOf[RUT_WithByNameAndFunction0],
    Seq(byNameAndFunction0Method -> Set(0))
  )

  "MockMetadataCache returnsValueClass registration" should {

    "be true for primitive-backed value class" in {
      MockMetadataCache.getReturnsValueClass(primitiveMethod) shouldBe Some(true)
    }

    "be true for reference-backed value class" in {
      MockMetadataCache.getReturnsValueClass(referenceMethod) shouldBe Some(true)
    }

    "be false for abstract type member bounded by AnyVal" in {
      MockMetadataCache.getReturnsValueClass(typeMemberMethod) shouldBe Some(false)
    }

    "be false for generic AnyVal bound method returning type variable" in {
      MockMetadataCache.getReturnsValueClass(genericBoundMethod) shouldBe Some(false)
    }
  }

  "MockMetadataCache returnType registration" should {

    "resolve String type alias to classOf[String]" in {
      MockMetadataCache.getReturnType(stringAliasMethod) shouldBe Some(classOf[String])
    }

    "record classOf[String] for plain String return" in {
      MockMetadataCache.getReturnType(stringReturnMethod) shouldBe Some(classOf[String])
    }
  }

  "ReflectionUtils.methodsWithLazyOrVarArgs" should {

    "detect by-name (idx=0) and vararg (idx=1) after macro registration" in {
      val methodInfos = ReflectionUtils.methodsWithLazyOrVarArgs(Seq(classOf[RUT_WithByNameAndVarArg]))
      methodInfos should not be empty
      val (_, indices) = methodInfos.head
      indices should contain(0)
      indices should contain(1)
    }

    "not treat Function0 arguments as by-name" in {
      val methodInfos = ReflectionUtils.methodsWithLazyOrVarArgs(Seq(classOf[RUT_WithByNameAndFunction0]))
      methodInfos.size shouldBe 1
      val (method, indices) = methodInfos.head
      method.getName shouldBe "byNameArg"
      indices shouldBe Set(0)
    }
  }

}
