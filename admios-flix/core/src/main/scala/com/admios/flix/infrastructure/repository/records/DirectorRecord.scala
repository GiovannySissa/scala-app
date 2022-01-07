package com.admios.flix.infrastructure.repository.records

import cats.data.ValidatedNel
import com.admios.flix.domain.movie.Director
import com.admios.flix.errors.InvalidInput
import io.chrisdavenport.fuuid.FUUID

final case class DirectorRecord(id: FUUID, name: String)

object DirectorRecord {

  val fromDomain: Director => DirectorRecord =
    director => DirectorRecord(id = director.id.value, name = director.name.value)

  val toDomain: DirectorRecord => ValidatedNel[InvalidInput, Director] =
    record =>
      Director(
        id   = record.id.toString(),
        name = record.name
      )
}
