package com.admios.flix.domain.movie

import cats.kernel.Eq
import com.admios.flix.errors.{InvalidInput, InvalidMovieId}
import io.chrisdavenport.fuuid.FUUID
import cats.syntax.either._

final case class MovieId private (value: FUUID) extends AnyVal

object MovieId {

  implicit val eq:Eq[MovieId] = Eq.fromUniversalEquals

  def apply(strId: String): Either[InvalidInput, MovieId] =
    FUUID.fromString(strId).bimap(_ => InvalidMovieId.of(strId), apply)
}
