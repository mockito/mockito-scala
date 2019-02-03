package org.mockito

import org.mockito.MockitoScalaSession.{ MockitoScalaSessionListener, UnexpectedInvocations }
import org.mockito.exceptions.misusing.{ UnexpectedInvocationException, UnnecessaryStubbingException }
import org.mockito.internal.stubbing.StubbedInvocationMatcher
import org.mockito.invocation.{ DescribedInvocation, Invocation, Location }
import org.mockito.listeners.MockCreationListener
import org.mockito.mock.MockCreationSettings
import org.mockito.quality.Strictness
import org.mockito.quality.Strictness.STRICT_STUBS
import org.mockito.session.MockitoSessionLogger

import scala.collection.JavaConverters._
import scala.collection.mutable

class MockitoScalaSession(name: String, strictness: Strictness, logger: MockitoSessionLogger) {
  private val listener       = new MockitoScalaSessionListener(strictness)
  private val mockitoSession = Mockito.mockitoSession().name(name).logger(logger).strictness(strictness).startMocking()

  Mockito.framework().addListener(listener)

  def finishMocking(t: Option[Throwable] = None): Unit =
    try {
      t.fold {
        mockitoSession.finishMocking()
        listener.reportIssues().foreach(_.report())
      } {
        case e: NullPointerException =>
          mockitoSession.finishMocking(e)
          listener.reportIssues().foreach {
            case unStubbedCalls: UnexpectedInvocations if unStubbedCalls.nonEmpty =>
              throw new UnexpectedInvocationException(s"""A NullPointerException was thrown, check if maybe related to
                   |$unStubbedCalls""".stripMargin,
                                                      e)
            case _ => throw e
          }
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

  trait Reporter {
    def report(): Unit
  }

  case class UnexpectedInvocations(invocations: Set[Invocation]) extends Reporter {
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

    def report(): Unit = if (nonEmpty) throw new UnexpectedInvocationException(toString)
  }

  case class UnusedStubbings(stubbings: Set[StubbedInvocationMatcher]) extends Reporter {
    def nonEmpty: Boolean = stubbings.nonEmpty

    override def toString: String =
      if (nonEmpty) {
        val locations = stubbings.zipWithIndex
          .map {
            case (stubbing, idx) => s"${idx + 1}. $stubbing ${stubbing.getLocation}"
          }
          .mkString("\n")
        s"""Unnecessary stubbings detected.
           |
           |Clean & maintainable test code requires zero unnecessary code.
           |Following stubbings are unnecessary (click to navigate to relevant line of code):
           |$locations
           |Please remove unnecessary stubbings or use 'lenient' strictness. More info: javadoc for UnnecessaryStubbingException class.""".stripMargin
      } else "No unexpected invocations found"

    def report(): Unit = if (nonEmpty) throw new UnnecessaryStubbingException(toString)
  }

  class MockitoScalaSessionListener(strictness: Strictness) extends MockCreationListener {
    def reportIssues(): Seq[Reporter] = {
      val mockDetails = mocks.toSet.map(MockitoSugar.mockingDetails)

      val stubbings = mockDetails
        .flatMap(_.getStubbings.asScala)
        .collect {
          case s: StubbedInvocationMatcher => s
        }

      val invocations = mockDetails.flatMap(_.getInvocations.asScala)

      val unexpectedInvocations = invocations
        .filterNot(_.isVerified)
        .filterNot(_.getMethod.getName.contains("$default$"))
        .filterNot(i => stubbings.exists(_.matches(i)))

      val unusedStubbings = stubbings.filterNot(sm => invocations.exists(sm.matches)).filter(!_.wasUsed())

      Seq(
        UnexpectedInvocations(unexpectedInvocations),
        UnusedStubbings(unusedStubbings)
      )
    }

    private val mocks = mutable.Set.empty[AnyRef]

    override def onMockCreated(mock: AnyRef, settings: MockCreationSettings[_]): Unit =
      if (!settings.isLenient && strictness != Strictness.LENIENT) mocks += mock
  }
}

object MockitoScalaLogger extends MockitoSessionLogger {
  override def log(hint: String): Unit = println(hint)
}
