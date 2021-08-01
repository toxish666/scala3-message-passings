package free.practice.log

object Headers {

  final case class Header(name: String) extends AnyVal

  final val GroupId = Header("GroupID")
  final val IdentityId = Header("IdentityID")
}
