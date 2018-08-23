package org.mockito

import java.lang.reflect.Modifier.{isAbstract, isFinal}

import org.mockito.exceptions.base.MockitoException
import org.mockito.exceptions.verification.SmartNullPointerException
import org.mockito.internal.util.ObjectMethodsGuru.isToStringMethod
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.Answers._

import scala.concurrent.Future
import scala.util.{Failure, Try}

trait DefaultAnswer extends Answer[Any] with Function[InvocationOnMock, Option[Any]] { self =>
  override def answer(invocation: InvocationOnMock): Any = apply(invocation).orNull

  def orElse(next: DefaultAnswer): DefaultAnswer = new DefaultAnswer {
    override def apply(invocation: InvocationOnMock): Option[Any] = self(invocation).orElse(next.apply(invocation))
  }
}

object DefaultAnswer {
  implicit val defaultAnswer: DefaultAnswer = DefaultParametersHandler orElse ReturnsDefaults orElse SmartNulls
}

object DefaultParametersHandler extends DefaultAnswer {
  override def apply(invocation: InvocationOnMock): Option[Any] =
    if (invocation.getMethod.getName.contains("$default$") && !isAbstract(invocation.getMethod.getModifiers))
      Some(invocation.callRealMethod())
    else None
}

object ReturnsDefaults extends DefaultAnswer {
  override def apply(invocation: InvocationOnMock): Option[Any] = Option(RETURNS_DEFAULTS.answer(invocation))
}

object CallsRealMethods extends DefaultAnswer {
  override def apply(invocation: InvocationOnMock): Option[Any] = Option(CALLS_REAL_METHODS.answer(invocation))
}

object SmartNulls extends DefaultAnswer {
  override def apply(invocation: InvocationOnMock): Option[Any] = {
    val returnType = invocation.getMethod.getReturnType

    if (!returnType.isPrimitive && !isFinal(returnType.getModifiers))
      Some(Mockito.mock(returnType, ThrowsSmartNullPointer(invocation)))
    else
      None
  }

  private case class ThrowsSmartNullPointer(unStubbedInvocation: InvocationOnMock) extends Answer[Any] {

    override def answer(currentInvocation: InvocationOnMock): Any =
      if (isToStringMethod(currentInvocation.getMethod))
        s"""SmartNull returned by this un-stubbed method call on a mock:
           |${unStubbedInvocation.toString}""".stripMargin
      else
        throw new SmartNullPointerException(
          s"""You have a NullPointerException because this method call was *not* stubbed correctly:
             |[$unStubbedInvocation] on the Mock [${unStubbedInvocation.getMock}]""".stripMargin)
  }
}

object ReturnsEmptyValues extends DefaultAnswer {
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

  override def apply(invocation: InvocationOnMock): Option[Any] = emptyValues.get(invocation.getMethod.getReturnType)
}
