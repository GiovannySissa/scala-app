package com.admios.flix.infrastructure.redis.records

import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.circe._
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class UserRecord(userId: FUUID, username: String)

object UserRecord {
  implicit val codec: Codec[UserRecord] = deriveCodec
}
