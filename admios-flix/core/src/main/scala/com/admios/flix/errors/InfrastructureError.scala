package com.admios.flix.errors

abstract class InfrastructureError(val message: MessageError) extends Exception(message.text)

final case class UnexpectedFailure(override val message: MessageError = MessageError("Something went wrong"))
    extends InfrastructureError(message)

final case class UserNameInUse(username: String)(
    override val message: MessageError = MessageError(s"The username $username is not available")
) extends InfrastructureError(message)

object UserNameInUse {
  def of(username: String): UserNameInUse = UserNameInUse(username)()
}

final case class InvalidUserOrPassword(
    override val message: MessageError = MessageError("Username or Password is invalid")
) extends InfrastructureError(message)

final case class TokenNotFound(
    override val message: MessageError = MessageError("Token not found")
) extends InfrastructureError(message)
