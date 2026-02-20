package org.mockito.captor

import org.mockito.internal.MacroDebug.debugResult
import org.mockito.internal.ScalaVersion
import org.mockito.internal.ScalaVersion.{ V2_12, V2_13 }

import scala.reflect.ClassTag
import scala.reflect.macros.blackbox

trait Captor[T] extends CaptorBase[T]

class WrapperCaptor[T: ClassTag] extends WrapperCaptorBase[T] with Captor[T]

object Captor {
  implicit def asCapture[T](c: Captor[T]): T = c.capture

  implicit def materializeValueClassCaptor[T]: Captor[T] = macro materializeValueClassCaptorMacro[T]

  def materializeValueClassCaptorMacro[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[Captor[T]] = {
    import c.universe.*
    val tpe          = weakTypeOf[T]
    val typeSymbol   = tpe.typeSymbol
    val isValueClass = typeSymbol.isClass && typeSymbol.asClass.isDerivedValueClass

    val r = if (isValueClass) c.Expr[Captor[T]] {
      val param = tpe.decls
        .collectFirst {
          case m: MethodSymbol if m.isPrimaryConstructor => m
        }
        .get
        .paramLists
        .head
        .head
      val paramType = tpe.decl(param.name).typeSignature.finalResultType

      val collectionConverters = ScalaVersion.Current match {
        case V2_12 => q"import _root_.scala.collection.JavaConverters._"
        case V2_13 => q"import _root_.scala.jdk.CollectionConverters._"
      }

      q"""
      new _root_.org.mockito.captor.Captor[$tpe] {

        $collectionConverters

        private val argumentCaptor = _root_.org.mockito.ArgumentCaptor.forClass(classOf[$paramType])

        override def capture: $tpe = new $tpe(argumentCaptor.capture())

        override def value: $tpe = new $tpe(argumentCaptor.getValue)

        override def values: List[$tpe] = argumentCaptor.getAllValues.asScala.map(v => new $tpe(v)).toList
      }
    """
    }
    else
      c.Expr[Captor[T]](q"new _root_.org.mockito.captor.WrapperCaptor[$tpe]")

    debugResult(c)("mockito-print-captor")(r.tree)
    r
  }
}
