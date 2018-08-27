package org.mockito

import java.util.function

import org.mockito.internal.handler.ScalaMockHandler.{ArgumentExtractor, Extractors}

import scala.reflect.runtime.universe._

private[mockito] object ReflectionUtils {

  def interfaces[T](implicit tag: TypeTag[T]): List[Class[_]] =
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
