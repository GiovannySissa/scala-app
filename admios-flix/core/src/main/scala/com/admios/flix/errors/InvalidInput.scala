package com.admios.flix.errors

sealed abstract class InvalidInput(error: MessageError) extends DomainError(error)

final case class InvalidMovieId private (invalidId: String)(
    val error: MessageError = MessageError(s"The id $invalidId is invalid")
) extends InvalidInput(error)

object InvalidMovieId {
  def of(invalidId: String): InvalidMovieId = InvalidMovieId(invalidId)()
}

final case class EmptyMovieTitle(val error: MessageError = MessageError("The movie title can't be empty"))
    extends InvalidInput(error)

final case class InvalidGenre private (invalid: String)(
    val error: MessageError = MessageError(s"The genre $invalid is non an valid option")
) extends InvalidInput(error)

object InvalidGenre {
  def of(invalid: String): InvalidGenre = InvalidGenre(invalid)()
}

final case class InvalidReleaseDate(error: MessageError = MessageError("The date can't be previous than current"))
    extends InvalidInput(error)


final case class InvalidDirectorId private (invalidId: String)(
  val error: MessageError = MessageError(s"The value: $invalidId is not accepted as a director Id")
) extends InvalidInput(error)

object InvalidDirectorId{
  def of(invalidId: String): InvalidDirectorId  = InvalidDirectorId(invalidId)()
}

final case class EmptyDirectorName(val error: MessageError = MessageError("The director must have non empty name"))
  extends InvalidInput(error)

final case class EmptyUsername(val error: MessageError = MessageError("The username must have non empty value"))
  extends InvalidInput(error)


final case class EmptyPassword(val error: MessageError = MessageError("The password must have non empty value"))
  extends InvalidInput(error)
