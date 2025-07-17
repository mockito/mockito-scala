package org.mockito

import org.mockito.invocation.InvocationOnMock
import ru.vyarus.java.generics.resolver.GenericsResolver

import java.lang.reflect.Field
import scala.util.control.NonFatal

/**
 * Utility methods for Java reflection operations, particularly for Mockito mocks.
 */
object JavaReflectionUtils {

  def resolveWithJavaGenerics(invocation: InvocationOnMock): Option[Class[_]] =
    try Some(GenericsResolver.resolve(invocation.getMock.getClass).`type`(invocation.method.getDeclaringClass).method(invocation.method).resolveReturnClass())
    catch {
      case _: Throwable => None
    }

  def setFinalStatic(field: Field, newValue: AnyRef): Unit =
    try {
      // Try to get Unsafe instance (works with both sun.misc.Unsafe and jdk.internal.misc.Unsafe)
      val unsafeClass: Class[_] =
        try
          Class.forName("sun.misc.Unsafe")
        catch {
          case _: ClassNotFoundException => Class.forName("jdk.internal.misc.Unsafe")
        }

      val unsafeField = unsafeClass.getDeclaredField("theUnsafe")
      unsafeField.setAccessible(true)
      val unsafe = unsafeField.get(null)

      // Get methods via reflection to handle both Unsafe implementations
      val staticFieldBaseMethod   = unsafeClass.getMethod("staticFieldBase", classOf[Field])
      val staticFieldOffsetMethod = unsafeClass.getMethod("staticFieldOffset", classOf[Field])
      val putObjectMethod         = unsafeClass.getMethod("putObject", classOf[Object], classOf[Long], classOf[Object])

      // Make the field accessible
      field.setAccessible(true)

      // Get base and offset for the field
      val base: Object = staticFieldBaseMethod.invoke(unsafe, field)
      val offset: Long = staticFieldOffsetMethod.invoke(unsafe, field).asInstanceOf[Long]

      // Set the field value directly
      putObjectMethod.invoke(unsafe, base, java.lang.Long.valueOf(offset), newValue)
    } catch {
      case NonFatal(e) =>
        throw new IllegalStateException(s"Cannot modify final field ${field.getName}", e)
    }

}
