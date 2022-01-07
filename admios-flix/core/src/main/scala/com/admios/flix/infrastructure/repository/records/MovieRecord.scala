package com.admios.flix.infrastructure.repository.records

import com.admios.flix.domain.movie.{Genre, Movie}
import io.chrisdavenport.fuuid.FUUID

import java.time.OffsetDateTime

final case class MovieRecord(
    id: FUUID,
    title: String,
    genre: Genre,
    director: DirectorRecord,
    releaseDate: OffsetDateTime
)

object MovieRecord {

  val fromDomain: Movie => MovieRecord = movie =>
    MovieRecord(
      id          = movie.id.value,
      title       = movie.title.value,
      genre       = movie.genre,
      director    = DirectorRecord.fromDomain(movie.director),
      releaseDate = movie.releaseDate.value.toOffsetDateTime
    )

}
