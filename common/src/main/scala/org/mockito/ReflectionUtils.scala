package org.mockito

import java.lang.reflect.Method

import org.mockito.invocation.InvocationOnMock
import org.scalactic.TripleEquals._
import ru.vyarus.java.generics.resolver.GenericsResolver

import scala.reflect.ClassTag
import scala.reflect.internal.Symbols
import scala.util.control.NonFatal

private[mockito] object ReflectionUtils {

  import scala.reflect.runtime.{ universe => ru }
  import ru._

  implicit def symbolToMethodSymbol(sym: Symbol): Symbols#MethodSymbol = sym.asInstanceOf[Symbols#MethodSymbol]

  private val mirror = runtimeMirror(getClass.getClassLoader)
  private val customMirror = mirror.asInstanceOf[{
    def methodToJava(sym: Symbols#MethodSymbol): Method
  }]

  implicit class InvocationOnMockOps(invocation: InvocationOnMock) {
    def returnType: Class[_] = {
      val method         = invocation.getMethod
      val javaReturnType = method.getReturnType

      if (javaReturnType == classOf[Object])
        resolveWithScalaGenerics(method)
          .orElse(resolveWithJavaGenerics(method))
          .getOrElse(javaReturnType)
      else javaReturnType
    }

    private def resolveWithScalaGenerics(method: Method): Option[Class[_]] =
      scala.util
        .Try {
          mirror
            .classSymbol(method.getDeclaringClass)
            .info
            .decls
            .collectFirst {
              case symbol
                  if isNonConstructorMethod(symbol) &&
                  customMirror.methodToJava(symbol) === method &&
                  symbol.returnType.typeSymbol.isClass =>
                mirror.runtimeClass(symbol.asMethod.returnType.typeSymbol.asClass)
            }
        }
        .toOption
        .flatten

    private def resolveWithJavaGenerics(method: Method): Option[Class[_]] =
      try Some(GenericsResolver.resolve(invocation.getMock.getClass).`type`(method.getDeclaringClass).method(method).resolveReturnClass())
      catch {
        // HACK for JVM 8 due to java.lang.InternalError: Malformed class name being thrown when calling getSimpleName on objects nested in
        // other objects
        case e: InternalError if e.getMessage == "Malformed class name" => None
        case NonFatal(_)                                                => None
      }
  }

  private def isNonConstructorMethod(d: ru.Symbol): Boolean = d.isMethod && !d.isConstructor

  def extraInterfaces[T](implicit $wtt: WeakTypeTag[T], $ct: ClassTag[T]): List[Class[_]] =
    scala.util
      .Try {
        val cls = clazz($ct)
        $wtt.tpe match {
          case RefinedType(types, _) =>
            types.map($wtt.mirror.runtimeClass).collect {
              case c: Class[_] if c.isInterface && c != cls => c
            }
          case _ => List.empty
        }
      }
      .toOption
      .getOrElse(List.empty)

  def methodsWithLazyOrVarArgs(classes: Seq[Class[_]]): Seq[(Method, Set[Int])] =
    classes.flatMap { clazz =>
      scala.util
        .Try {
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
        }
        .toOption
        .getOrElse(Seq.empty)
    }
}
