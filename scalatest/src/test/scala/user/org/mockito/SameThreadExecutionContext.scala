package user.org.mockito

import scala.concurrent.ExecutionContext

class SameThreadExecutionContext extends ExecutionContext {
  override def execute(runnable: Runnable): Unit = runnable.run()

  override def reportFailure(cause: Throwable): Unit = throw cause
}

object SameThreadExecutionContext {
  implicit val Instance: ExecutionContext = new SameThreadExecutionContext
}
