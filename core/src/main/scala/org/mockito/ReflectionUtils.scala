package org.mockito

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

}
