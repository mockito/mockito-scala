package org.mockito

import java.lang.reflect.Method
import java.util.function

import org.mockito.internal.handler.ScalaMockHandler.{ArgumentExtractor, Extractors}
import org.mockito.invocation.InvocationOnMock
import ru.vyarus.java.generics.resolver.GenericsResolver
import org.scalactic.TripleEquals._

import scala.reflect.internal.Symbols

private[mockito] object ReflectionUtils {

  import scala.reflect.runtime.{ universe => ru }
  import ru._

  private val mirror = runtimeMirror(getClass.getClassLoader)
  private val customMirror = mirror.asInstanceOf[{
    def methodToJava(sym: Symbols#MethodSymbol): Method
  }]

  implicit class InvocationOnMockOps(invocation: InvocationOnMock) {
    def returnType: Class[_] = {
      val method         = invocation.getMethod
      val clazz          = method.getDeclaringClass
      val javaReturnType = invocation.getMethod.getReturnType

      if (javaReturnType == classOf[Object])
        mirror
          .classSymbol(clazz)
          .info
          .decls
          .filter(d => d.isMethod && !d.isConstructor)
          .find(d => customMirror.methodToJava(d.asInstanceOf[Symbols#MethodSymbol]) === method)
          .map(_.asMethod)
          .filter(_.returnType.typeSymbol.isClass)
          .map(methodSymbol => mirror.runtimeClass(methodSymbol.returnType.typeSymbol.asClass))
          .orElse(resolveGenerics)
          .getOrElse(javaReturnType)
      else javaReturnType
    }

    private def resolveGenerics: Option[Class[_]] =
      scala.util.Try {
        GenericsResolver.resolve(invocation.getMock.getClass).`type`(clazz).method(invocation.getMethod).resolveReturnClass()
      }.toOption
  }

  def interfaces[T](implicit tag: WeakTypeTag[T]): List[Class[_]] =
    tag.tpe match {
      case RefinedType(types, _) =>
        types.map(tag.mirror.runtimeClass).collect {
          case c: Class[_] if c.isInterface => c
        }
      case _ => List.empty
    }

  def markMethodsWithLazyArgs(clazz: Class[_]): Unit =
    Extractors.computeIfAbsent(
      clazz,
      new function.Function[Class[_], ArgumentExtractor] {
        override def apply(t: Class[_]): ArgumentExtractor = {
          val mirror = runtimeMirror(clazz.getClassLoader)

          val symbol = mirror.classSymbol(clazz)

          val methodsWithLazyArgs = symbol.info.decls
            .collect {
              case s if s.isMethod =>
                (s.name.toString, s.typeSignature.paramLists.flatten.zipWithIndex.collect {
                  case (p, idx) if p.typeSignature.toString.startsWith("=>") => idx
                }.toSet)
            }
            .toMap
            .filter(_._2.nonEmpty)

          ArgumentExtractor(methodsWithLazyArgs)
        }
      }
    )
}
