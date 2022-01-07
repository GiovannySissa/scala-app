package com.admios.flix.models

import cats.effect.Sync
import cats.syntax.option._
import com.admios.flix.domain.movie.{Genre, Movie => MovieDomain}
import com.admios.flix.infrastructure.repository.records.MovieRecord
import io.chrisdavenport.fuuid.FUUID
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import org.http4s.rho.swagger.models.{Model, ModelImpl, RefProperty, StringProperty}

import java.time.ZonedDateTime
import java.util.UUID

final case class Movie(
    id: UUID,
    title: String,
    genre: Genre,
    director: Director,
    releaseDate: ZonedDateTime
)

object Movie {
  val fromRecord: MovieRecord => Movie = record =>
    Movie(
      id          = FUUID.Unsafe.toUUID(record.id),
      title       = record.title,
      genre       = record.genre,
      director    = Director.fromRecord(record.director),
      releaseDate = record.releaseDate.toZonedDateTime
    )

  implicit def entityEncoder[F[_]: Sync]: EntityEncoder[F, Movie] = jsonEncoderOf[F, Movie]


  val toDomain: Movie => MovieDomain = model =>
    MovieDomain(
      id          = ???,
      title       = ???,
      genre       = ???,
      director    = ???,
      releaseDate = ???
    )

  implicit val codec: Codec[Movie] = deriveCodec

  val model: Set[Model] = Set(
    ModelImpl(
      id          = "movie",
      id2         = "Movie",
      description = "Movie definition".some,
      `type`      = "object".some,
      name        = "Movie".some,
      required    = List("id", "title", "genre", "director", "releaseDate"),
      properties = Map(
        "id" -> StringProperty(
          required    = true,
          description = "movie's id".some,
          enums       = Set.empty,
          format      = "java.util.UUID".some
        ),
        "title" -> StringProperty(
          required    = true,
          description = "movie's title".some,
          enums       = Set.empty
        ),
        "genre" -> StringProperty(
          required    = true,
          description = "movie's genre".some,
          enums       = Genre.values.map(_.value).toSet
        ),
        "director" -> RefProperty(
          ref      = "Director",
          required = true,
          title    = "Director".some
        ),
        "releaseDate" -> StringProperty(
          required    = true,
          description = "movie's release date".some,
          enums       = Set.empty,
          format      = "java.time.ZonedDateTime".some
        )
      ),
      example = """
          |{
          |  "id": "39e72634-1e0e-4d05-b828-9517a791bf60",
          |  "title": "Movie Title",
          |  "genre": "Adventure",
          |  "releaseDate": "2021-09-09T15:07:19.629079Z",
          |  "director": {
          |    "id": "4ed08f08-aca1-43a0-bf64-74e206da48ad",
          |    "name": "Steven Spielberg"
          |  }
          |}
          |""".stripMargin.some
    )
  )
}
