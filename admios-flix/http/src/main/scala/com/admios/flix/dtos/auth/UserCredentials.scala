package com.admios.flix.dtos.auth

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class UserCredentials(username: String, password: String)

object UserCredentials {

  implicit val codec: Codec[UserCredentials] = deriveCodec
}
