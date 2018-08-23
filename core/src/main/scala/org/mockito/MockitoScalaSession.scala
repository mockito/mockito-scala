package org.mockito

import org.mockito.MockitoScalaSession.UnexpectedInvocationsMockListener
import org.mockito.exceptions.misusing.UnexpectedInvocationException
import org.mockito.invocation.{DescribedInvocation, Invocation, Location}
import org.mockito.listeners.MockCreationListener
import org.mockito.mock.MockCreationSettings
import org.mockito.quality.Strictness
import org.mockito.quality.Strictness.STRICT_STUBS
import org.mockito.session.MockitoSessionLogger

import scala.collection.mutable
import scala.collection.JavaConverters._

class MockitoScalaSession(name: String, strictness: Strictness, logger: MockitoSessionLogger) {
  private val listener       = new UnexpectedInvocationsMockListener
  private val mockitoSession = Mockito.mockitoSession().name(name).logger(logger).strictness(strictness).startMocking()

  Mockito.framework().addListener(listener)

  def finishMocking(t: Option[Throwable] = None): Unit =
    try {
      t.fold {
        mockitoSession.finishMocking()
        listener.reportUnStubbedCalls().reportUnexpectedInvocations()
      } {
        case e: NullPointerException =>
          mockitoSession.finishMocking(e)
          val unStubbedCalls = listener.reportUnStubbedCalls()
          if (unStubbedCalls.nonEmpty)
            throw new UnexpectedInvocationException(s"""A NullPointerException was thrown, check if maybe related to
               |$unStubbedCalls""".stripMargin,
                                                    e)
          else throw e
        case other =>
          mockitoSession.finishMocking(other)
          throw other
      }
    } finally {
      Mockito.framework().removeListener(listener)
    }

  def run[T](block: => T): T =
    try {
      val result = block
      finishMocking()
      result
    } catch {
      case e: Throwable =>
        finishMocking(Some(e))
        throw e
    }
}

object MockitoScalaSession {
  def apply(name: String = "<Unnamed Session>",
            strictness: Strictness = STRICT_STUBS,
            logger: MockitoSessionLogger = MockitoScalaLogger): MockitoScalaSession =
    new MockitoScalaSession(name, strictness, logger)

  object SyntheticLocation extends Location
  object SyntheticMethodInvocation extends DescribedInvocation {
    override def getLocation: Location = SyntheticLocation
  }

  case class UnexpectedInvocations(invocations: Set[Invocation]) {
    def nonEmpty: Boolean = invocations.nonEmpty

    override def toString: String =
      if (nonEmpty) {
        val locations = invocations.zipWithIndex
          .map {
            case (invocation, idx) => s"${idx + 1}. $invocation ${invocation.getLocation}"
          }
          .mkString("\n")
        s"""Unexpected invocations found
           |
           |The following invocations are unexpected (click to navigate to relevant line of code):
           |$locations
           |Please make sure you aren't missing any stubbing or that your code actually does what you want""".stripMargin
      } else "No unexpected invocations found"

    def reportUnexpectedInvocations(): Unit =
      if (nonEmpty) throw new UnexpectedInvocationException(toString)
  }

  class UnexpectedInvocationsMockListener extends MockCreationListener {
    def reportUnStubbedCalls(): UnexpectedInvocations =
      UnexpectedInvocations(
        mocks
          .map(MockitoSugar.mockingDetails)
          .flatMap(_.getInvocations.asScala)
          .filter(_.stubInfo() == null)
          .filterNot(_.isVerified)
          .filterNot(_.getMethod.getName.contains("$default$"))
          .toSet
      )

    private val mocks = mutable.Set.empty[AnyRef]

    override def onMockCreated(mock: AnyRef, settings: MockCreationSettings[_]): Unit = mocks += mock
  }
}

object MockitoScalaLogger extends MockitoSessionLogger {
  override def log(hint: String): Unit = println(hint)
}
