package com.admios.flix.dtos

import cats.data.ValidatedNel
import com.admios.flix.domain.movie.Director
import com.admios.flix.errors.InvalidInput
import com.admios.flix.infrastructure.repository.records.DirectorRecord
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class DirectorDTO(
    id: String,
    name: String
)

object DirectorDTO {
  implicit val codec: Codec[DirectorDTO] = deriveCodec

  val toDomain: DirectorDTO => ValidatedNel[InvalidInput, Director] = dto =>
    Director(
      id   = dto.id,
      name = dto.name
    )

  val fromRecord: DirectorRecord => DirectorDTO = record => DirectorDTO(id = record.id.show, name = record.name)
}
