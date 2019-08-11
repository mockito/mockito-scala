package org.mockito.stubbing

import org.mockito.exceptions.base.MockitoException
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues
import org.mockito.invocation.InvocationOnMock

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.{ Failure, Try }

object ReturnsEmptyValues extends DefaultAnswer {
  private val javaEmptyValuesAndPrimitives = new ReturnsMoreEmptyValues

  private[mockito] lazy val emptyValues: Map[Class[_], AnyRef] = Map(
    classOf[Option[_]]      -> Option.empty,
    classOf[List[_]]        -> List.empty,
    classOf[Set[_]]         -> Set.empty,
    classOf[Seq[_]]         -> Seq.empty,
    classOf[Iterable[_]]    -> Iterable.empty,
    classOf[IndexedSeq[_]]  -> IndexedSeq.empty,
    classOf[Iterator[_]]    -> Iterator.empty,
    classOf[LazyList[_]]    -> LazyList.empty,
    classOf[Vector[_]]      -> Vector.empty,
    classOf[Try[_]]         -> Failure(new MockitoException("Auto stub provided by mockito-scala")),
    classOf[Future[_]]      -> Future.failed(new MockitoException("Auto stub provided by mockito-scala")),
    classOf[BigDecimal]     -> BigDecimal(0),
    classOf[BigInt]         -> BigInt(0),
    classOf[StringBuilder]  -> new StringBuilder,
    classOf[Map[_, _]]      -> Map.empty,
    classOf[ListBuffer[_]]  -> ListBuffer.empty,
    classOf[mutable.Seq[_]] -> ListBuffer.empty,
    classOf[mutable.Set[_]] -> mutable.HashSet.empty,
    classOf[Either[_, _]]   -> Left("Auto stub provided by mockito-scala")
  )

  override def apply(invocation: InvocationOnMock): Option[Any] =
    Option(javaEmptyValuesAndPrimitives.answer(invocation)).orElse(emptyValues.get(invocation.getMethod.getReturnType))
}
