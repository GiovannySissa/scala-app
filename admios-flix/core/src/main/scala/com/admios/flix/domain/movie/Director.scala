package com.admios.flix.domain.movie

import cats.data.ValidatedNel
import cats.syntax.apply._
import cats.syntax.either._
import com.admios.flix.errors.{EmptyDirectorName, InvalidDirectorId, InvalidInput}
import io.chrisdavenport.fuuid.FUUID
import mouse.boolean._

final case class Director private (id: DirectorId, name: DirectorName)

object Director {

  def apply(name: String, id: String): ValidatedNel[InvalidInput, Director] =
    (
      DirectorId(id).toValidatedNel,
      DirectorName(name).toValidatedNel
    ).mapN(new Director(_, _))
}

final case class DirectorId private (value: FUUID) extends AnyVal

object DirectorId {

  def apply(id: String): Either[InvalidInput, DirectorId] =
    FUUID.fromString(id).bimap(_ => InvalidDirectorId.of(id), apply)
}

final case class DirectorName private (value: String) extends AnyVal

object DirectorName {

  def apply(name: String): Either[InvalidInput, DirectorName] =
    name.nonEmpty.either(EmptyDirectorName(), new DirectorName(name))
}
