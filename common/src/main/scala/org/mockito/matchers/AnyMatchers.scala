package org.mockito.matchers

import org.mockito.{ArgumentMatchers => JavaMatchers}

private[mockito] trait AnyMatchers {

  /** List matcher that use Scala List to avoid compile errors like
   * Error:(40, 60) type mismatch;
   * found   : List[String] (in java.util)
   * required: List[?]      (in scala.collection.immutable)
   *
   * when trying to do something like ArgumentMatchers.anyList[String]()
   *
   */
  def anyList[T]: List[T] = any[List[T]]

  /** Seq matcher that use Scala Seq to avoid compile errors like
   * Error:(40, 60) type mismatch;
   * found   : List[String] (in java.util)
   * required: Seq[?]      (in scala.collection.immutable)
   *
   * when trying to do something like ArgumentMatchers.anyList[String]()
   *
   */
  def anySeq[T]: Seq[T] = any[Seq[T]]

  /** Iterable matcher that use Scala Iterable to avoid compile errors like
   * Error:(40, 60) type mismatch;
   * found   : Iterable[String] (in java.util)
   * required: Iterable[?]      (in scala.collection.immutable)
   *
   * when trying to do something like ArgumentMatchers.anyIterable[String]()
   *
   */
  def anyIterable[T]: Iterable[T] = any[Iterable[T]]

  /** Set matcher that use Scala Set to avoid compile errors like
   * Error:(40, 60) type mismatch;
   * found   : Set[String] (in java.util)
   * required: Set[?]      (in scala.collection.immutable)
   *
   * when trying to do something like ArgumentMatchers.anySet[String]()
   *
   */
  def anySet[T]: Set[T] = any[Set[T]]

  /** Map matcher that use Scala Map to avoid compile errors like
   * Error:(40, 60) type mismatch;
   * found   : Map[String, String] (in java.util)
   * required: Map[?]      (in scala.collection.immutable)
   *
   * when trying to do something like ArgumentMatchers.anyMap[String, String]()
   *
   */
  def anyMap[K, V]: Map[K, V] = any[Map[K, V]]

  /**
   * Delegates to <code>ArgumentMatchers.any()</code>, it's main purpose is to remove the () out of
   * the method call, if you try to do that directly on the test you get this error
   *
   * Error:(71, 46) polymorphic expression cannot be instantiated to expected type;
   * found   : [T]()T
   * required: String
   * when you try to something like ArgumentMatchers.any
   *
   */
  def any[T]: T = JavaMatchers.any[T]()

  /**
   * Alias for [[ org.mockito.matchers.AnyMatchers.any[T] ]]
   */
  def *[T]: T = any[T]

  /**
   * Delegates to <code>ArgumentMatchers.anyByte()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place as any[T] would do the job just fine
   *
   */
  def anyByte: Byte = JavaMatchers.anyByte

  /**
   * Delegates to <code>ArgumentMatchers.anyBoolean()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place as any[T] would do the job just fine
   *
   */
  def anyBoolean: Boolean = JavaMatchers.anyBoolean

  /**
   * Delegates to <code>ArgumentMatchers.anyChar()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place as any[T] would do the job just fine
   *
   */
  def anyChar: Char = JavaMatchers.anyChar

  /**
   * Delegates to <code>ArgumentMatchers.anyDouble()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place as any[T] would do the job just fine
   *
   */
  def anyDouble: Double = JavaMatchers.anyDouble

  /**
   * Delegates to <code>ArgumentMatchers.anyInt()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place as any[T] would do the job just fine
   *
   */
  def anyInt: Int = JavaMatchers.anyInt

  /**
   * Delegates to <code>ArgumentMatchers.anyFloat()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place as any[T] would do the job just fine
   *
   */
  def anyFloat: Float = JavaMatchers.anyFloat

  /**
   * Delegates to <code>ArgumentMatchers.anyShort()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place as any[T] would do the job just fine
   *
   */
  def anyShort: Short = JavaMatchers.anyShort

  /**
   * Delegates to <code>ArgumentMatchers.anyLong()</code>, it's only here so we expose all the `ArgumentMatchers`
   * on a single place as any[T] would do the job just fine
   *
   */
  def anyLong: Long = JavaMatchers.anyLong
}
