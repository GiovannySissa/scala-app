package com.admios.flix.domain.services

import com.admios.flix.domain.movie.{Genre, Movie, MovieId}
import com.admios.flix.infrastructure.repository.records.MovieRecord

trait MovieServices[F[_]] {
  def create(movie: Movie): F[Movie]
  def delete(movieId: MovieId): F[Unit]
  def update(movie: Movie): F[Movie]
  def findByGenre(genres: List[Genre])(limit: Int, offset: Int): F[List[MovieRecord]]
}

object MovieServices {

  def apply[F[_]: MovieServices]: MovieServices[F] = implicitly
}
