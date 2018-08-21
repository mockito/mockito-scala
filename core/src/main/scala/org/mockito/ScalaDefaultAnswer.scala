package org.mockito

import java.lang.reflect.Modifier.{ isAbstract, isFinal }

import org.mockito.exceptions.base.MockitoException
import org.mockito.exceptions.verification.SmartNullPointerException
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues
import org.mockito.internal.util.ObjectMethodsGuru.isToStringMethod
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import scala.util.{ Failure, Try }

object ScalaDefaultAnswer extends Answer[Any] {

  private val delegate = new ReturnsMoreEmptyValues

  override def answer(invocation: InvocationOnMock): Any =
    if (invocation.getMethod.getName.contains("$default$") && !isAbstract(invocation.getMethod.getModifiers))
      invocation.callRealMethod()
    else
      Option(delegate.answer(invocation))
        .orElse(emptyValues.get(invocation.getMethod.getReturnType))
        .orElse(smartNull(invocation))
        .orNull

  private def smartNull(invocation: InvocationOnMock): Option[Any] = {
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
    classOf[BigDecimal]     -> BigDecimal(0),
    classOf[BigInt]         -> BigInt(0),
    classOf[StringBuilder]  -> StringBuilder.newBuilder
  )

}
