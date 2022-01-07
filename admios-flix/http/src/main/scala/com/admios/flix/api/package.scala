package com.admios.flix

import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

package object api {

  implicit def entityDecoder[F[_]: Sync, DTO: Decoder]: EntityDecoder[F, DTO] =
    jsonOf[F, DTO]

  implicit def collectionEntityEncoder[F[_]: Sync, DTO: Encoder]: EntityEncoder[F, List[DTO]] =
    jsonEncoderOf[F, List[DTO]]
}
