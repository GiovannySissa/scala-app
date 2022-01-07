package com.admios.flix.infrastructure.interpreters

import cats.effect.{Async, Resource}
import cats.syntax.apply._
import cats.syntax.functor._
import com.admios.flix.domain.movie.{Genre, Movie, MovieId}
import com.admios.flix.domain.services.MovieServices
import com.admios.flix.infrastructure.repository.daos.{DirectorSQL, MovieSQL}
import com.admios.flix.infrastructure.repository.records.{DirectorRecord, MovieRecord}
import doobie.implicits._
import doobie.util.transactor.Transactor

final class LiveServices[F[_]: Async] private (xa: Transactor[F]) extends MovieServices[F] {

  override def create(movie: Movie): F[Movie] =
    (
      storeDirector(DirectorRecord.fromDomain(movie.director)),
      storeMovie(MovieRecord.fromDomain(movie))
    ).mapN(_ + _)
      .transact(xa)
      .as(movie)

  private def storeDirector(record: DirectorRecord) =
    DirectorSQL
      .insert(record)
      .run

  private def storeMovie(record: MovieRecord) =
    MovieSQL
      .insert(record)
      .run

  override def delete(movieId: MovieId): F[Unit] =
    MovieSQL
      .delete(movieId.value)
      .run
      .transact(xa)
      .void

  override def update(movie: Movie): F[Movie] =
    MovieSQL
      .update(MovieRecord.fromDomain(movie))
      .run
      .transact(xa)
      .as(movie)

  override def findByGenre(genres: List[Genre])(limit: Int, offset: Int): F[List[MovieRecord]] =
    MovieSQL
      .findByGenre(genres)(limit, offset)
      .to[List]
      .transact(xa)
}

object LiveServices {

  def apply[F[_]: Async](xa: Transactor[F]): MovieServices[F] = new LiveServices[F](xa)

  def make[F[_]: Async](xa: Transactor[F]): Resource[F, MovieServices[F]] =
    Resource.eval(
      Async[F].pure(apply(xa))
    )
}
