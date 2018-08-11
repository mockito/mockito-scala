package org.mockito
import org.mockito.MockitoScalaSession.IgnoreDefaultArgumentsMockListener
import org.mockito.internal.stubbing.StubbedInvocationMatcher
import org.mockito.invocation.{DescribedInvocation, Location}
import org.mockito.listeners.MockCreationListener
import org.mockito.mock.MockCreationSettings
import org.mockito.quality.Strictness
import org.mockito.quality.Strictness.STRICT_STUBS
import org.mockito.session.MockitoSessionLogger

import scala.collection.mutable
import scala.collection.JavaConverters._

class MockitoScalaSession(name: String, strictness: Strictness, logger: MockitoSessionLogger) {
  private val listener       = new IgnoreDefaultArgumentsMockListener
  private val mockitoSession = Mockito.mockitoSession().name(name).logger(logger).strictness(strictness).startMocking()

  Mockito.framework().addListener(listener)

  def finishMocking(): Unit = {
    listener.ignoreDefaultMethods()
    Mockito.framework().removeListener(listener)
    mockitoSession.finishMocking()
  }
}

object MockitoScalaSession {
  def apply(name: String = "<Unnamed Session>",
            strictness: Strictness = STRICT_STUBS,
            logger: MockitoSessionLogger = println) = new MockitoScalaSession(name, strictness, logger)

  object SyntheticLocation extends Location
  object SyntheticMethodInvocation extends DescribedInvocation {
    override def getLocation: Location = SyntheticLocation
  }

  class IgnoreDefaultArgumentsMockListener extends MockCreationListener {

    private val mocks = mutable.Set.empty[AnyRef]

    override def onMockCreated(mock: AnyRef, settings: MockCreationSettings[_]): Unit = mocks += mock

    def ignoreDefaultMethods() =
      mocks
        .map(MockitoSugar.mockingDetails)
        .flatMap(_.getStubbings.asScala)
        .filter(_.getInvocation.getMethod.getName.contains("$default$"))
        .filter(!_.wasUsed())
        .foreach {
          case s: StubbedInvocationMatcher => s.markStubUsed(SyntheticMethodInvocation)
          case _                           => ()
        }
  }
}