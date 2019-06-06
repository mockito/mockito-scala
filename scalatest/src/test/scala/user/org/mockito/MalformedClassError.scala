package user.org.mockito

import user.org.mockito.MalformedClassError._
import org.mockito.IdiomaticMockito
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpecLike }

import scala.concurrent.Future

// Mockito fails with 'Malformed class name' when using java 8 - #117
class MalformedClassError extends WordSpecLike with Matchers with IdiomaticMockito with ScalaFutures {

  "A example" should {
    val client = mock[Client]

    "run" in {

      val wrapper                           = new ClientWrapper(client)
      implicit val allowedEvidence: Allowed = Permissions.Allowed
      client[Future]("a") shouldReturn Future.successful("response")
      wrapper.call[Future]("a").futureValue shouldEqual "response"
    }
  }
}

object MalformedClassError {

  sealed trait Permissions
  object Permissions {
    final case object Allowed extends Permissions
    final case object Denied  extends Permissions
  }
  type Allowed = Permissions.Allowed.type

  class ClientWrapper(client: Client) {
    def call[F[_]](field: String)(implicit ev: Allowed): F[String] = client(field)
  }

  trait Client {
    def apply[F[_]](field: String)(implicit ev: Allowed): F[String]
  }
}
