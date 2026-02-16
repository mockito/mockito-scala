package org.mockito.internal

trait ValueClassExtractor[VC] extends Serializable {
  def isValueClass: Boolean = true
  def extract(vc: VC): Any
  def extractAs[T](vc: VC): T = extract(vc).asInstanceOf[T]
}

class NormalClassExtractor[T] extends ValueClassExtractor[T] {
  override def isValueClass: Boolean = false
  override def extract(vc: T): Any   = vc
}

class ReflectionExtractor[VC] extends ValueClassExtractor[VC] {
  override def extract(vc: VC): Any = {
    val constructorParam = vc.getClass.getConstructors.head.getParameters.head
    val accessor         = vc.getClass.getMethods
      .filter(m => m.getName == constructorParam.getName || m.getName.endsWith("$$" + constructorParam.getName))
      .head
    accessor.setAccessible(true)
    accessor.invoke(vc)
  }
}

object ValueClassExtractor extends ValueClassExtractorCompat {
  def apply[T: ValueClassExtractor]: ValueClassExtractor[T] = implicitly[ValueClassExtractor[T]]
}
