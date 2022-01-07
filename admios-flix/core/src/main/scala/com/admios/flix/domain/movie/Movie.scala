package com.admios.flix.domain.movie

import cats.data.ValidatedNel
import cats.syntax.either._
import cats.syntax.apply._
import com.admios.flix.errors.InvalidInput

import java.time.ZonedDateTime

final case class Movie private (
    id: MovieId,
    title: MovieTitle,
    genre: Genre,
    director: Director,
    releaseDate: ReleaseDate
)

object Movie {

  def apply(
      id: String,
      title: String,
      genre: String,
      director: Director,
      releaseDate: ZonedDateTime
  ): ValidatedNel[InvalidInput, Movie] =
    (
      MovieId(id).toValidatedNel,
      MovieTitle(title).toValidatedNel,
      Genre(genre).toValidatedNel,
      ReleaseDate(releaseDate).toValidatedNel
    ).mapN { case (vId, vTitle, vGenre, vReleaseDate) =>
      new Movie(id = vId, title = vTitle, genre = vGenre, director = director, releaseDate = vReleaseDate)
    }

}
