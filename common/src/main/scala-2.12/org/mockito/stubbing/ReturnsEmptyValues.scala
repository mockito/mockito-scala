package org.mockito
package stubbing

import org.mockito.exceptions.base.MockitoException
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues
import org.mockito.invocation.InvocationOnMock

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.{ Failure, Try }

object ReturnsEmptyValues extends DefaultAnswer {
  private val javaEmptyValuesAndPrimitives = new ReturnsMoreEmptyValues

  private[mockito] lazy val emptyValues: Map[Class[?], AnyRef] = Map(
    classOf[Option[?]]      -> Option.empty,
    classOf[List[?]]        -> List.empty,
    classOf[Set[?]]         -> Set.empty,
    classOf[Seq[?]]         -> Seq.empty,
    classOf[Iterable[?]]    -> Iterable.empty,
    classOf[Traversable[?]] -> Traversable.empty,
    classOf[IndexedSeq[?]]  -> IndexedSeq.empty,
    classOf[Iterator[?]]    -> Iterator.empty,
    classOf[Stream[?]]      -> Stream.empty,
    classOf[Vector[?]]      -> Vector.empty,
    classOf[Try[?]]         -> Failure(new MockitoException("Auto stub provided by mockito-scala")),
    classOf[Future[?]]      -> Future.failed(new MockitoException("Auto stub provided by mockito-scala")),
    classOf[BigDecimal]     -> BigDecimal(0),
    classOf[BigInt]         -> BigInt(0),
    classOf[StringBuilder]  -> StringBuilder.newBuilder,
    classOf[Map[?, ?]]      -> Map.empty,
    classOf[ListBuffer[?]]  -> ListBuffer.empty,
    classOf[mutable.Seq[?]] -> ListBuffer.empty,
    classOf[mutable.Set[?]] -> mutable.HashSet.empty,
    classOf[Either[?, ?]]   -> Left("Auto stub provided by mockito-scala")
  )

  override def apply(invocation: InvocationOnMock): Option[Any] =
    Option(javaEmptyValuesAndPrimitives.answer(invocation)).orElse(emptyValues.get(invocation.returnType))
}
