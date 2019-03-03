package org.mockito.stubbing

import org.mockito.exceptions.base.MockitoException
import org.mockito.invocation.InvocationOnMock
import org.mockito.Answers._
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues

import scala.concurrent.Future
import scala.util.{ Failure, Try }

trait DefaultAnswer extends Answer[Any] with Function[InvocationOnMock, Option[Any]] { self =>
  override def answer(invocation: InvocationOnMock): Any = apply(invocation).orNull

  def orElse(next: DefaultAnswer): DefaultAnswer = new DefaultAnswer {
    override def apply(invocation: InvocationOnMock): Option[Any] = self(invocation).orElse(next(invocation))
  }
}

object DefaultAnswer {
  implicit val defaultAnswer: DefaultAnswer = ReturnsSmartNulls

  def apply(from: Answer[_]): DefaultAnswer = new DecoratedAnswer(from)
  def apply(value: Any): DefaultAnswer = new DefaultAnswer {
    override def apply(i: InvocationOnMock): Option[Any] = Some(value)
  }
}

class DecoratedAnswer(from: Answer[_]) extends DefaultAnswer {
  override def apply(invocation: InvocationOnMock): Option[Any] = Option(from.answer(invocation))
}

object ReturnsDefaults  extends DecoratedAnswer(RETURNS_DEFAULTS)
object ReturnsDeepStubs extends DecoratedAnswer(RETURNS_DEEP_STUBS)
object CallsRealMethods extends DecoratedAnswer(CALLS_REAL_METHODS)
object ReturnsEmptyValues extends DefaultAnswer {
  private val javaEmptyValuesAndPrimitives = new ReturnsMoreEmptyValues

  private[mockito] lazy val emptyValues: Map[Class[_], AnyRef] = Map(
    classOf[Option[_]]      -> Option.empty,
    classOf[List[_]]        -> List.empty,
    classOf[Set[_]]         -> Set.empty,
    classOf[Seq[_]]         -> Seq.empty,
    classOf[Iterable[_]]    -> Iterable.empty,
    classOf[Traversable[_]] -> Traversable.empty,
    classOf[IndexedSeq[_]]  -> IndexedSeq.empty,
    classOf[Iterator[_]]    -> Iterator.empty,
    classOf[Stream[_]]      -> Stream.empty,
    classOf[Vector[_]]      -> Vector.empty,
    classOf[Try[_]]         -> Failure(new MockitoException("Auto stub provided by mockito-scala")),
    classOf[Future[_]]      -> Future.failed(new MockitoException("Auto stub provided by mockito-scala")),
    classOf[BigDecimal]     -> BigDecimal(0),
    classOf[BigInt]         -> BigInt(0),
    classOf[StringBuilder]  -> StringBuilder.newBuilder
  )

  override def apply(invocation: InvocationOnMock): Option[Any] =
    Option(javaEmptyValuesAndPrimitives.answer(invocation)).orElse(emptyValues.get(invocation.getMethod.getReturnType))
}
