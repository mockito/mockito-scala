package org.mockito.internal

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * Runtime cache for per-method mock metadata. Populated at compile time via [[org.mockito.internal.MockMethodMetadata]] inline/macro calls at each `mock[T]` call site.
 *
 * Keys and contract:
 *   - `byName`: keyed by declaring `Class[?]`, stores `Seq[(Method, Set[Int])]`:
 *       - outer `Seq`: one entry per method on that class with metadata
 *       - inner `Set[Int]`: zero-based parameter indices that are by-name or vararg for that method
 *         (example: for `def foo(x: => Int, ys: String*, z: () => String)`, indices are `Set(0, 1)`; `z` is plain `Function0`, so excluded)
 *   - `returnsValueClass`: keyed by `Method`, stores whether the declared Scala return type extends `AnyVal`.
 *   - `returnType`: keyed by `Method`, stores the concrete declared return class when JVM erasure gives `Object` but the Scala source type is more specific (e.g. abstract type
 *     aliases, path-dependent types).
 */
object MockMetadataCache {
  private val byNameCache            = new ConcurrentHashMap[Class[?], Seq[(Method, Set[Int])]]()
  private val returnsValueClassCache = new ConcurrentHashMap[Method, java.lang.Boolean]()
  private val returnTypeCache        = new ConcurrentHashMap[Method, Class[?]]()

  /** Get by-name/vararg metadata for a class: `Seq[(method, zero-based by-name/vararg indices)]`, if registered. */
  def getByName(clazz: Class[?]): Option[Seq[(Method, Set[Int])]] =
    Option(byNameCache.get(clazz))

  /** Register by-name/vararg metadata (`Set[Int]` are zero-based parameter indices) once per class (idempotent best-effort). */
  def registerByName(clazz: Class[?], info: Seq[(Method, Set[Int])]): Unit =
    byNameCache.putIfAbsent(clazz, info)

  /** Get value-like return classification for a method, if registered. */
  def getReturnsValueClass(method: Method): Option[Boolean] =
    Option(returnsValueClassCache.get(method)).map(_.booleanValue())

  /** Register value-like return classification for methods (first writer wins). */
  def registerReturnsValueClass(info: Seq[(Method, Boolean)]): Unit =
    info.foreach { case (method, v) => returnsValueClassCache.putIfAbsent(method, v) }

  /** Get concrete return class metadata for an erased/ambiguous method signature, if registered. */
  def getReturnType(method: Method): Option[Class[?]] =
    Option(returnTypeCache.get(method))

  /** Register concrete return classes for methods (first writer wins). */
  def registerReturnType(info: Seq[(Method, Class[?])]): Unit =
    info.foreach { case (method, cls) => returnTypeCache.putIfAbsent(method, cls) }
}
