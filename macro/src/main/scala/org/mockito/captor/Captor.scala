package org.mockito.captor

import org.mockito.exceptions.verification.ArgumentsAreDifferent
import org.mockito.{ clazz, ArgumentCaptor }

import scala.collection.JavaConverters._
import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.reflect.macros.blackbox

trait Captor[T] {

  def capture: T

  def value: T

  def values: List[T]

  def hasCaptured(expectation: T): Unit =
    if (expectation != value) throw new ArgumentsAreDifferent(s"Got [$value] instead of [$expectation]")
}

class WrapperCaptor[T: ClassTag] extends Captor[T] {

  private val argumentCaptor: ArgumentCaptor[T] = ArgumentCaptor.forClass(clazz)

  override def capture: T = argumentCaptor.capture()

  override def value: T = argumentCaptor.getValue

  override def values: List[T] = argumentCaptor.getAllValues.asScala.toList
}

object Captor {

  implicit def asCapture[T](c: Captor[T]): T = c.capture

  implicit def materializeValueClassCaptor[T]: Captor[T] = macro materializeValueClassCaptorMacro[T]

  def materializeValueClassCaptorMacro[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[Captor[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]

    val isValueClass = tpe.typeSymbol.asClass.isDerivedValueClass

    val r = if (isValueClass) c.Expr[Captor[T]] {
      val param = tpe.decls
        .collectFirst {
          case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
        }
        .get
        .paramLists
        .head
        .head
      val paramType = tpe.decl(param.name).typeSignature.finalResultType

      q"""
      new org.mockito.captor.Captor[$tpe] {

        import scala.collection.JavaConverters._

        private val argumentCaptor = org.mockito.ArgumentCaptor.forClass(classOf[$paramType])

        override def capture: $tpe = new $tpe(argumentCaptor.capture())

        override def value: $tpe = new $tpe(argumentCaptor.getValue)

        override def values: List[$tpe] = argumentCaptor.getAllValues.asScala.map(v => new $tpe(v)).toList
      }
    """
    } else
      c.Expr[Captor[T]](q"new org.mockito.captor.WrapperCaptor[$tpe]")

    if (c.settings.contains("mockito-print-captor")) println(show(r.tree))
    r
  }
}
