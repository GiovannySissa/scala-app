package com.admios.flix.domain.movie

import cats.syntax.either._
import com.admios.flix.errors.{InvalidGenre, InvalidInput}
import enumeratum.{Circe, Enum, EnumEntry}
import io.circe.{Decoder, Encoder}

sealed abstract class Genre(val value: String) extends EnumEntry {

  override def entryName: String = value
}

object Genre extends Enum[Genre] {

  final case object Comedy      extends Genre("Comedy")
  final case object Action      extends Genre("Action")
  final case object Adventure   extends Genre("Adventure")
  final case object Documentary extends Genre("Documentary")
  final case object Drama       extends Genre("Drama")
  final case object Horror      extends Genre("Horror")
  final case object Romantic    extends Genre("Romantic")

  override def values: IndexedSeq[Genre] = findValues

  def apply(genre: String): Either[InvalidInput, Genre] =
    withNameEither(genre)
      .leftMap(noGenre => InvalidGenre.of(noGenre.notFoundName))

  implicit val circeEncoder: Decoder[Genre] = Circe.decodeCaseInsensitive(this)
  implicit val encoder: Encoder[Genre] = Circe.encoder(this)
}
