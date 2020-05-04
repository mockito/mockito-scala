package user.org.mockito

import org.mockito.IdiomaticMockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

abstract class Abstract
trait Trait

trait TraitExtendsAbstract             extends Abstract
abstract class AbstractExtendsTrait    extends Trait
trait TraitExtendsTrait                extends Trait
abstract class AbstractExtendsAbstract extends Abstract

class IncorrectSubTypingError extends AnyWordSpecLike with Matchers with IdiomaticMockito {

  "mock" should {
    "create a mock with the right inheritance - TraitExtendsAbstract" in {
      val aMock: TraitExtendsAbstract = mock[TraitExtendsAbstract]
      aMock.asInstanceOf[Abstract]
    }

    "create a mock with the right inheritance - AbstractExtendsAbstract" in {
      val aMock: AbstractExtendsAbstract = mock[AbstractExtendsAbstract]
      aMock.asInstanceOf[Abstract]
    }

    "create a mock with the right inheritance - AbstractExtendsTrait" in {
      val aMock: AbstractExtendsTrait = mock[AbstractExtendsTrait]
      aMock.asInstanceOf[Trait]
    }

    "create a mock with the right inheritance - TraitExtendsTrait" in {
      val aMock: TraitExtendsTrait = mock[TraitExtendsTrait]
      aMock.asInstanceOf[Trait]
    }
  }
}
