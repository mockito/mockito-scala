package org.mockito.internal

import scala.reflect.ClassTag
import scala.reflect.macros.blackbox

trait ValueClassWrapper[VC] extends Serializable {
  def isValueClass: Boolean = true
  def wrap(vc: VC): Any
  def wrapAs[T](vc: VC): T = wrap(vc).asInstanceOf[T]
}

class NormalClassWrapper[T] extends ValueClassWrapper[T] {
  override def isValueClass: Boolean = false
  override def wrap(vc: T): Any      = vc
}

class ReflectionWrapper[VC: ClassTag] extends ValueClassWrapper[VC] {
  private def clazz[T](implicit classTag: ClassTag[T]): Class[T] = classTag.runtimeClass.asInstanceOf[Class[T]]

  override def wrap(vc: VC): Any =
    clazz.getConstructors
      .collectFirst {
        case c if c.getParameterCount == 1 => c.newInstance(vc.asInstanceOf[Object])
      }
      .getOrElse(throw new RuntimeException(s"Can't find a constructor for $clazz that takes a single param"))
}

object ValueClassWrapper {
  def apply[T: ValueClassWrapper]: ValueClassWrapper[T] = implicitly[ValueClassWrapper[T]]

  implicit def instance[VC]: ValueClassWrapper[VC] = macro materialise[VC]

  def materialise[VC: c.WeakTypeTag](c: blackbox.Context): c.Expr[ValueClassWrapper[VC]] = {
    import c.universe._
    val tpe          = weakTypeOf[VC]
    val typeSymbol   = tpe.typeSymbol
    val isValueClass = typeSymbol.isClass && typeSymbol.asClass.isDerivedValueClass

    val r =
      if (isValueClass)
        c.Expr[ValueClassWrapper[VC]](q"new _root_.org.mockito.internal.ReflectionWrapper[$tpe]")
      else
        c.Expr[ValueClassWrapper[VC]](q"new _root_.org.mockito.internal.NormalClassWrapper[$tpe]")

    if (c.settings.contains("mockito-print-wrapper")) println(show(r.tree))

    r
  }
}
