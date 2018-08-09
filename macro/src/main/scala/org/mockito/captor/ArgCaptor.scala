package org.mockito.captor

import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.reflect.macros.blackbox

trait ArgCaptor[T] {

  def capture: T

  def value: T

  def values: List[T]

  def <->(expectation: T): Unit =
    if (expectation != value) throw new AssertionError(s"Got [$value] instead of [$expectation]")
}

object ArgCaptor {

  implicit def asCapture[T](c: ArgCaptor[T]): T = c.capture

  implicit def materializeValueClassCaptor[T]: ArgCaptor[T] = macro materializeValueClassCaptorMacro[T]

  def materializeValueClassCaptorMacro[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[ArgCaptor[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]

    val param = tpe.decls
      .collectFirst {
        case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
      }
      .get
      .paramLists
      .head
      .head

    val paramType = tpe.decl(param.name).typeSignature.finalResultType

    c.Expr[ArgCaptor[T]] {
      q"""
      new org.mockito.captor.ArgCaptor[$tpe] {

        import scala.collection.JavaConverters._

        private val argumentCaptor = org.mockito.ArgumentCaptor.forClass(classOf[$paramType])

        override def capture: $tpe = new $tpe(argumentCaptor.capture())

        override def value: $tpe = new $tpe(argumentCaptor.getValue)

        override def values: List[$tpe] = argumentCaptor.getAllValues.asScala.map(v => new $tpe(v)).toList
      }
    """
    }
  }
}
