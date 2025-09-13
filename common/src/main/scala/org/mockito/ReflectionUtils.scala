package org.mockito

import org.mockito.JavaReflectionUtils.resolveWithJavaGenerics
import org.mockito.invocation.InvocationOnMock
import org.scalactic.TripleEquals.*

import java.lang.reflect.Method
import scala.reflect.ClassTag
import scala.reflect.internal.Symbols
import scala.util.Try as uTry

object ReflectionUtils {
  import scala.reflect.runtime.universe as ru
  import ru.*

  implicit def symbolToMethodSymbol(sym: Symbol): Symbols#MethodSymbol = sym.asInstanceOf[Symbols#MethodSymbol]

  private val mirror       = runtimeMirror(getClass.getClassLoader)
  private val customMirror = mirror.asInstanceOf[{
    def methodToJava(sym: Symbols#MethodSymbol): Method
  }]

  private[mockito] def returnType(invocation: InvocationOnMock): Class[?] = {
    val javaReturnType = invocation.method.getReturnType

    if (javaReturnType == classOf[Object])
      resolveWithScalaGenerics(invocation)
        .orElse(resolveWithJavaGenerics(invocation))
        .getOrElse(javaReturnType)
    else javaReturnType
  }

  private[mockito] def returnsValueClass(invocation: InvocationOnMock): Boolean =
    findTypeSymbol(invocation).exists(_.returnType.typeSymbol.isDerivedValueClass)

  private def resolveWithScalaGenerics(invocation: InvocationOnMock): Option[Class[?]] =
    uTry {
      findTypeSymbol(invocation)
        .filter(_.returnType.typeSymbol.isClass)
        .map(_.asMethod.returnType.typeSymbol.asClass)
        .map(mirror.runtimeClass)
    }.toOption.flatten

  private def findTypeSymbol(invocation: InvocationOnMock) =
    uTry {
      mirror
        .classSymbol(invocation.method.getDeclaringClass)
        .info
        .decls
        .collectFirst {
          case symbol if isNonConstructorMethod(symbol) && customMirror.methodToJava(symbol) === invocation.method => symbol
        }
    }.toOption.flatten

  private def isNonConstructorMethod(d: ru.Symbol): Boolean = d.isMethod && !d.isConstructor

  def extraInterfaces[T](implicit $wtt: WeakTypeTag[T], $ct: ClassTag[T]): List[Class[?]] =
    uTry {
      val cls = clazz($ct)
      $wtt.tpe match {
        case RefinedType(types, _) =>
          types.map($wtt.mirror.runtimeClass).collect {
            case c: Class[?] if c.isInterface && c != cls => c
          }
        case _ => List.empty
      }
    }.toOption
      .getOrElse(List.empty)

  def methodsWithLazyOrVarArgs(classes: Seq[Class[?]]): Seq[(Method, Set[Int])] =
    classes.flatMap { clazz =>
      uTry {
        mirror
          .classSymbol(clazz)
          .info
          .members
          .collect {
            case symbol if isNonConstructorMethod(symbol) =>
              symbol -> symbol.typeSignature.paramLists.flatten.zipWithIndex.collect {
                case (p, idx) if p.typeSignature.toString.startsWith("=>") => idx
                case (p, idx) if p.typeSignature.toString.endsWith("*")    => idx
              }.toSet
          }
          .collect {
            case (symbol, indices) if indices.nonEmpty => customMirror.methodToJava(symbol) -> indices
          }
          .toSeq
      }.toOption
        .getOrElse(Seq.empty)
    }

}
