package com.admios.flix.authentication.config

import io.circe.Decoder
import io.circe.config.syntax.durationDecoder
import io.circe.generic.semiauto.deriveDecoder

import scala.concurrent.duration.FiniteDuration

final case class PasswordSalt(value: String) extends AnyVal {
  override def toString: String = "<<hide content>>"
}

final case class JwtKey(value: String) extends AnyVal {
  override def toString: String = "<<hide content>>"
}

final case class JwtClaim(value: String) extends AnyVal {
  override def toString: String = "<<hide content>>"
}

final case class TokenExpiration(time: FiniteDuration)

final case class HttpAuthenticationConfig(
    salt: PasswordSalt,
    jwtSecretKey: JwtKey,
    jwtClaim: JwtClaim,
    tokenExpiration: TokenExpiration
)

object HttpAuthenticationConfig {

  implicit val passwordSaltDecoder: Decoder[PasswordSalt] = deriveDecoder
  implicit val jwtSecretKeyDecoder: Decoder[JwtKey] = deriveDecoder
  implicit val jwtClaimDecoder: Decoder[JwtClaim] = deriveDecoder
  implicit val tokenExpirationDecoder: Decoder[TokenExpiration] = deriveDecoder
  implicit val httpAuthenticationDecoder: Decoder[HttpAuthenticationConfig] = deriveDecoder
}
