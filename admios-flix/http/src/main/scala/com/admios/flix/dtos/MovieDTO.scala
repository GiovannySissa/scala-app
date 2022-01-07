package com.admios.flix.dtos

import cats.data.ValidatedNel
import com.admios.flix.domain.movie.Movie
import com.admios.flix.errors.InvalidInput
import com.admios.flix.infrastructure.repository.records.MovieRecord
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

import java.time.ZonedDateTime

final case class MovieDTO(
    id: String,
    title: String,
    genre: String,
    director: DirectorDTO,
    releaseDate: ZonedDateTime
) extends DTO

object MovieDTO {
  implicit val codec: Codec[MovieDTO] = deriveCodec

  val toDomain: MovieDTO => ValidatedNel[InvalidInput, Movie] = dto =>
    DirectorDTO.toDomain(dto.director) andThen { director =>
      Movie(
        id          = dto.id,
        title       = dto.title,
        genre       = dto.genre,
        director    = director,
        releaseDate = dto.releaseDate
      )
    }

  val fromRecord: MovieRecord => MovieDTO = record =>
    MovieDTO(
      id          = record.id.show,
      title       = record.title,
      genre       = record.genre.value,
      director    = DirectorDTO.fromRecord(record.director),
      releaseDate = record.releaseDate.toZonedDateTime
    )
}
