package free.practice

case class Ctx(
  rcRequestId: Option[String] = None
)

object Ctx {

  sealed trait Identity
  case class AuthorizedUser(groupId: String, identityId: String) extends Identity
  case class Guest(endpointId: String) extends Identity

}
