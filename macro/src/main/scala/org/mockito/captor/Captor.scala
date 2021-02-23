package org.mockito.captor

import org.mockito.exceptions.base.MockitoAssertionError
import org.mockito.exceptions.verification.{ ArgumentsAreDifferent, TooFewActualInvocations, TooManyActualInvocations }
import org.mockito.internal.MacroDebug.debugResult
import org.mockito.internal.ScalaVersion
import org.mockito.internal.ScalaVersion.{ V2_11, V2_12, V2_13 }
import org.mockito.{ clazz, ArgumentCaptor }
import org.scalactic.Equality
import org.scalactic.TripleEquals._

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.reflect.macros.blackbox
import scala.util.{ Failure, Try }

trait Captor[T] {
  def capture: T

  def value: T

  def values: List[T]

  def hasCaptured(expectations: T*)(implicit $eq: Equality[T]): Unit = {
    val elementResult = Try {
      expectations.zip(values).foreach { case (e, v) =>
        if (e !== v) throw new ArgumentsAreDifferent(s"Got [$v] instead of [$e]")
      }
    }

    val sizeResult = Try {
      (expectations.size, values.size) match {
        case (es, vs) if es - vs > 0 => throw new TooFewActualInvocations(s"Also expected ${es - vs} more: [${expectations.drop(vs).mkString(", ")}]")
        case (es, vs) if es - vs < 0 => throw new TooManyActualInvocations(s"Also got ${vs - es} more: [${values.drop(es).mkString(", ")}]")
        case _                       => None
      }
    }

    (elementResult, sizeResult) match {
      case (Failure(ef), Failure(sf: MockitoAssertionError)) => throw new MockitoAssertionError(sf, ef.getMessage)
      case (_, Failure(sf))                                  => throw sf
      case (Failure(ef), _)                                  => throw ef
      case _                                                 =>
    }
  }
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
    val tpe          = weakTypeOf[T]
    val typeSymbol   = tpe.typeSymbol
    val isValueClass = typeSymbol.isClass && typeSymbol.asClass.isDerivedValueClass

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

      val collectionConverters = ScalaVersion.Current match {
        case V2_11 | V2_12 => q"import _root_.scala.collection.JavaConverters._"
        case V2_13         => q"import _root_.scala.jdk.CollectionConverters._"
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
