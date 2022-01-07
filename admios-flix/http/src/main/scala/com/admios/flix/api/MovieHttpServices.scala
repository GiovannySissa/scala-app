package com.admios.flix.api

import cats.effect.Async
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.admios.flix.authentication.User
import com.admios.flix.domain.movie.{Genre, MovieId}
import com.admios.flix.domain.services.MovieServices
import com.admios.flix.dtos.MovieDTO
import com.admios.flix.errors.InvalidGenre
import io.chrisdavenport.fuuid.http4s.FUUIDVar
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, QueryParamDecoder}

final class MovieHttpServices[F[_]: Async: MovieServices] private extends Http4sDsl[F] {

  object PageMatcher   extends OptionalQueryParamDecoderMatcher[Int]("pageSize")
  object OffsetMatcher extends OptionalQueryParamDecoderMatcher[Int]("offset")

  implicit val genreQueryParamDec: QueryParamDecoder[Genre] =
    QueryParamDecoder[String].map(Genre.withName)

  object GenreMatcher extends OptionalMultiQueryParamDecoderMatcher[Genre]("genre")

  def routes: AuthedRoutes[User, F] = AuthedRoutes.of {
    // ar := Authenticated Request
    case ar @ POST -> Root as user =>
      ar.req.decode[MovieDTO] { dto =>
        for {
          movie    <- MovieDTO.toDomain(dto).domainErrorLift[F]
          _        <- MovieServices[F].create(movie)
          response <- Created()
        } yield response
      }
    case ar @ PUT -> Root as user =>
      ar.req.decode[MovieDTO] { dto =>
        for {
          movie    <- MovieDTO.toDomain(dto).domainErrorLift[F]
          _        <- MovieServices[F].update(movie)
          response <- NoContent()
        } yield response
      }
    case GET -> Root :? GenreMatcher(genres) :? PageMatcher(pageOpt) :? OffsetMatcher(
          offsetOpt
        ) as user =>
      for {
        g <- genres.leftMap(_.map(err => InvalidGenre.of(err.message))).domainErrorLift[F]
        limit = pageOpt.getOrElse(10)
        offset = offsetOpt.getOrElse(0)
        movies   <- MovieServices[F].findByGenre(g)(limit, offset)
        response <- Ok(movies.map(MovieDTO.fromRecord))
      } yield response

    case DELETE -> Root / FUUIDVar(movieId) as user =>
      for {
        _        <- MovieServices[F].delete(MovieId(movieId))
        response <- Ok("movie deleted!")
      } yield response

  }
}

object MovieHttpServices {

  def apply[F[_]: Async: MovieServices]: MovieHttpServices[F] = new MovieHttpServices[F]
}
