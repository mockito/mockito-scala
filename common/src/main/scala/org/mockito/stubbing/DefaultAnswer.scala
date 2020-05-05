package org.mockito.stubbing

import org.mockito.exceptions.base.MockitoException
import org.mockito.invocation.InvocationOnMock
import org.mockito.Answers._
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues

import scala.concurrent.Future
import scala.util.{ Failure, Try }

trait DefaultAnswer extends Answer[Any] with Function[InvocationOnMock, Option[Any]] with Serializable { self =>
  override def answer(invocation: InvocationOnMock): Any = apply(invocation).orNull

  def orElse(next: DefaultAnswer): DefaultAnswer =
    new DefaultAnswer {
      override def apply(invocation: InvocationOnMock): Option[Any] = self(invocation).orElse(next(invocation))
    }
}

object DefaultAnswer {
  implicit val defaultAnswer: DefaultAnswer = ReturnsSmartNulls

  def apply(from: Answer[_]): DefaultAnswer = new DecoratedAnswer(from)

  def apply(a: InvocationOnMock => Any): DefaultAnswer =
    DefaultAnswer(new Answer[Any] {
      override def answer(invocation: InvocationOnMock): Any = a(invocation)
    })

  def apply(value: Any): DefaultAnswer =
    new DefaultAnswer {
      override def apply(i: InvocationOnMock): Option[Any] = Some(value)
    }
}

class DecoratedAnswer(from: Answer[_]) extends DefaultAnswer {
  override def apply(invocation: InvocationOnMock): Option[Any] = Option(from.answer(invocation))
}

object ReturnsDefaults  extends DecoratedAnswer(RETURNS_DEFAULTS)
object ReturnsDeepStubs extends DecoratedAnswer(RETURNS_DEEP_STUBS)
object CallsRealMethods extends DecoratedAnswer(CALLS_REAL_METHODS)
