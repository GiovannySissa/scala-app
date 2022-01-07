package com.admios.flix.infrastructure.interpreters

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.instances.list._
import cats.syntax.eq._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import com.admios.flix.domain.movie.{Director, Genre, Movie, MovieId}
import com.admios.flix.domain.services.MovieServices
import com.admios.flix.infrastructure.repository.records.MovieRecord
import io.chrisdavenport.fuuid.FUUID

import java.time.ZonedDateTime
import scala.util.Random

final class SwaggerServices[F[_]: Sync] private extends MovieServices[F] {

  val moviesRef: F[Ref[F, List[Movie]]] =
    for {
      ref    <- Ref.of[F, List[Movie]](List.empty)
      values <- initialValues
      _      <- ref.set(values)
    } yield ref

  // create random initial values
  private def initialValues: F[List[Movie]] = (0 to Random.nextInt(5) + 1).toList.traverse { i =>
    for {
      id <- FUUID.randomFUUID[F]
      director <-
        FUUID
          .randomFUUID[F]
          .flatMap(dId => Director.apply(id = dId.show, name = s"Fake Director $i").domainErrorLift[F])
      movie <- Movie(
                 id          = id.show,
                 title       = s"Fake title $i",
                 genre       = Genre.values.apply(i).value,
                 director    = director,
                 releaseDate = ZonedDateTime.now().minusDays(i.toLong)
               ).domainErrorLift[F]
    } yield { movie }

  }

  // todo not ignore if exist
  override def create(movie: Movie): F[Movie] =
    for {
      ref <- moviesRef
      _   <- ref.update(act => act :+ movie)
    } yield movie

  override def delete(movieId: MovieId): F[Unit] =
    for {
      ref <- moviesRef
      _   <- ref.update(act => act.filter(_.id =!= movieId))
    } yield ()

  override def update(movie: Movie): F[Movie] =
    for {
      ref <- moviesRef
      _   <- ref.update(act => act :+ movie)
    } yield movie

  override def findByGenre(genres: List[Genre])(limit: Int, offset: Int): F[List[MovieRecord]] =
    moviesRef.flatMap(movies =>
      movies.get.map(
        _.filter(m => genres.contains(m.genre))
          .map(found => MovieRecord.fromDomain(found))
      )
    )
}

object SwaggerServices {

  def apply[F[_]: Sync]: MovieServices[F] = new SwaggerServices[F]
}
