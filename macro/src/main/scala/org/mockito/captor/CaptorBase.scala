package org.mockito.captor

import org.mockito.exceptions.base.MockitoAssertionError
import org.mockito.exceptions.verification.{ ArgumentsAreDifferent, TooFewActualInvocations, TooManyActualInvocations }
import org.mockito.{ clazz, ArgumentCaptor }
import org.scalactic.Equality
import org.scalactic.TripleEquals.*

import scala.jdk.CollectionConverters.*
import scala.reflect.ClassTag
import scala.util.{ Failure, Try }

/**
 * Base trait for argument capturing with shared runtime logic. Shared across Scala 2 and Scala 3.
 */
trait CaptorBase[T] {
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

/**
 * Default captor implementation wrapping Mockito's ArgumentCaptor. Shared across Scala 2 and Scala 3.
 */
abstract class WrapperCaptorBase[T: ClassTag] extends CaptorBase[T] {
  private val argumentCaptor: ArgumentCaptor[T] = ArgumentCaptor.forClass(clazz)

  override def capture: T = argumentCaptor.capture()

  override def value: T = argumentCaptor.getValue

  override def values: List[T] = argumentCaptor.getAllValues.asScala.toList
}
