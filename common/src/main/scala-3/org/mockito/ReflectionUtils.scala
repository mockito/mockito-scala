package org.mockito

import org.mockito.JavaReflectionUtils.resolveWithJavaGenerics
import org.mockito.internal.MockMethodMetadata
import org.mockito.internal.MockMetadataCache
import org.mockito.invocation.InvocationOnMock

import java.lang.reflect.{ Method, TypeVariable }
import scala.reflect.ClassTag

object ReflectionUtils {

  /**
   * Get the return type of a method invocation, resolving generics when JVM erasure yields `Object`.
   *
   * Scala 3 strategy (in order):
   *   1. compile-time metadata from [[MockMetadataCache.getReturnType]]
   *   2. runtime Java generic resolution via [[org.mockito.JavaReflectionUtils.resolveWithJavaGenerics]]
   *   3. raw JVM return type (`Object`) as a fallback
   *
   * This is conceptually equivalent to Scala 2's "recover a more specific type than Object" path, but uses compile-time metadata instead of runtime Scala reflection
   * (`WeakTypeTag`/`scala.reflect.runtime.universe`).
   */
  private[mockito] def returnType(invocation: InvocationOnMock): Class[?] = {
    val method         = invocation.method
    val javaReturnType = method.getReturnType

    if (javaReturnType == classOf[Object])
      MockMetadataCache
        .getReturnType(method)
        .orElse(resolveWithJavaGenerics(invocation))
        .getOrElse(javaReturnType)
    else javaReturnType
  }

  /**
   * Check if a method should be treated as value-like for null/default handling.
   *
   * Scala 3 strategy (in order):
   *   1. primitive fast-path (`returnType.isPrimitive`)
   *   2. compile-time metadata from [[MockMetadataCache.getReturnsValueClass]]
   *   3. conservative JVM fallback:
   *      - generic `TypeVariable` return => `false` (erased/ambiguous)
   *      - otherwise `classOf[AnyVal].isAssignableFrom(returnType)`
   *
   * This mirrors Scala 2 intent ("identify returns that require value-like handling") while replacing runtime Scala reflection with compile-time metadata plus conservative runtime
   * checks.
   */
  private[mockito] def returnsValueClass(invocation: InvocationOnMock): Boolean =
    val method     = invocation.method
    val returnType = method.getReturnType
    returnType.isPrimitive || MockMetadataCache
      .getReturnsValueClass(method)
      .getOrElse {
        method.getGenericReturnType match {
          case _: TypeVariable[?] => false
          case _                  => classOf[AnyVal].isAssignableFrom(returnType)
        }
      }

  /**
   * Extract extra interfaces from an intersection/refined type at compile time.
   *
   * Example: `mock[Foo & Bar]` yields `List(classOf[Bar])`.
   */
  inline def extraInterfaces[T: ClassTag]: List[Class[?]] =
    MockMethodMetadata.extraInterfacesImpl[T]

  /**
   * Find methods with lazy (by-name) parameters or varargs. In Scala 3, this reads from metadata cache populated at compile time by the [[org.mockito.internal.MockMethodMetadata]]
   * inline macro called during mock creation.
   *
   * Returned indices are consumed by `ScalaMockHandler` to unwrap by-name/vararg arguments while keeping plain `Function0` arguments distinct.
   */
  def methodsWithLazyOrVarArgs(classes: Seq[Class[?]]): Seq[(Method, Set[Int])] =
    classes.flatMap { clazz =>
      MockMetadataCache.getByName(clazz).getOrElse(Seq.empty)
    }
}
