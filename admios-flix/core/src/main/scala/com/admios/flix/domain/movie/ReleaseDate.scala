package com.admios.flix.domain.movie

import com.admios.flix.errors.{InvalidInput, InvalidReleaseDate}
import mouse.boolean._

import java.time.{ZoneOffset, ZonedDateTime}

final case class ReleaseDate private (value: ZonedDateTime) extends AnyVal

object ReleaseDate {

  def apply(date: ZonedDateTime): Either[InvalidInput, ReleaseDate] =
    // just to provide a fake validation that case could happen in a real scenario
    date
      .isAfter(ZonedDateTime.now(ZoneOffset.UTC))
      .either(
        InvalidReleaseDate(),
        new ReleaseDate(date)
      )
}
