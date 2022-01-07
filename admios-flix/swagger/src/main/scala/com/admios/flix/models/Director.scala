package com.admios.flix.models

import cats.effect.Sync
import cats.syntax.option._
import com.admios.flix.infrastructure.repository.records.DirectorRecord
import io.chrisdavenport.fuuid.FUUID
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import org.http4s.rho.swagger.models.{Model, ModelImpl, StringProperty}

import java.util.UUID

final case class Director(
    id: UUID,
    string: String
)

object Director {
  val fromRecord: DirectorRecord => Director = record =>
    Director(
      id     = FUUID.Unsafe.toUUID(record.id),
      string = record.name
    )

  implicit def entityEncoder[F[_]: Sync]: EntityEncoder[F, Director] = jsonEncoderOf[F, Director]
  implicit val codec: Codec[Director] = deriveCodec

  val model: Set[Model] = Set(
    ModelImpl(
      id          = "Director",
      id2         = "Director",
      description = "Director representation".some,
      properties = Map(
        "id" -> StringProperty(
          required    = true,
          description = "Director's identification".some,
          enums       = Set.empty,
          format      = "java.util.UUID".some
        ),
        "name" -> StringProperty(
          required    = true,
          description = "Director's name".some,
          enums       = Set.empty,
          format      = "String".some
        )
      ),
      example = """
          |{
          |  "director": {
          |    "id": "4ed08f08-aca1-43a0-bf64-74e206da48ad",
          |    "name": "Steven Spielberg"
          |  }
          |}
          |""".stripMargin.some
    )
  )

}
