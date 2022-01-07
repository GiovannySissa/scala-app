package com.admios.flix.api

import cats.effect.Sync
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.admios.flix.domain.movie.{Genre, MovieId}
import com.admios.flix.domain.services.MovieServices
import com.admios.flix.infrastructure.interpreters.SwaggerServices
import com.admios.flix.models.{Movie => MovieModel}
import org.http4s.circe.jsonOf
import org.http4s.rho.RhoRoutes
import org.http4s.rho.swagger.SwaggerSupport

import java.util.UUID

final class MovieDoc[F[_]: Sync: MovieServices] private (swaggerSupport: SwaggerSupport[F]) {

  import swaggerSupport._

  private val tag = "movie"

  def docRoutes: RhoRoutes[F] = new RhoRoutes[F] {
    val movieId = pathVar[UUID](id = "movieId", description = "Indicates the unique id for a movie")
    val genres = paramD[Option[String]](name = "genre", description = "allows filter by movie")

    "this route allows get all movies stored, you may include genre filter query parameter and also define page size result" **
      tag @@ GET /tag +? genres |>> { g: Option[String] =>
        for {
          movies   <- MovieServices[F].findByGenre(g.map(Genre.withName).toList)(0, 0)
          response <- Ok(movies.map(record => MovieModel.fromRecord(record)))
        } yield response
      }
    "This route allows create a new movie" **
      tag @@ POST / tag ^ jsonOf[F, MovieModel] |>> { movie: MovieModel =>
        MovieServices[F].create(MovieModel.toDomain(movie)).flatMap(_ => Created(movie))
      }

    "This route allows update an existing movie" **
      tag @@ PUT / tag ^ jsonOf[F, MovieModel] |>> { movie: MovieModel =>
        MovieServices[F].update(MovieModel.toDomain(movie)).flatMap(_ => Created(movie))
      }

    "This route allows delete an existing movie" **
      tag @@ DELETE / tag / movieId |>> { movieId: UUID =>
        for {
          movieId  <- MovieId(movieId.toString).liftTo[F]
          _        <- MovieServices[F].delete(movieId)
          response <- Ok()
        } yield response

      }
  }
}

object MovieDoc {

  def apply[F[_]: Sync](swaggerSupport: SwaggerSupport[F]): MovieDoc[F] = {
    implicit val swaggerServicesImpl: MovieServices[F] = SwaggerServices[F]
    new MovieDoc[F](swaggerSupport)
  }
}
