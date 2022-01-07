package com.admios.flix.domain.movie

import com.admios.flix.errors.{EmptyMovieTitle, InvalidInput}
import mouse.boolean._

final case class MovieTitle(value: String) extends AnyVal

object MovieTitle{

  def apply(title: String):Either[InvalidInput, MovieTitle] =
    title.nonEmpty.either(EmptyMovieTitle(),  new MovieTitle(title))
}
