package com.admios.flix.authentication.interpreters

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.admios.flix.authentication.Tokens
import com.admios.flix.authentication.config.{HttpAuthenticationConfig, JwtKey, TokenExpiration}
import dev.profunktor.auth.jwt._
import io.chrisdavenport.fuuid.FUUID
import io.circe.syntax._
import pdi.jwt._

class LiveTokens[F[_]: Sync] private (jwtSecretKey: JwtKey, tokenExpiration: TokenExpiration)(implicit
    val clock: java.time.Clock
) extends Tokens[F] {

  override def create: F[JwtToken] =
    for {
      uuid <- FUUID.randomFUUID[F]
      claim <- Sync[F].delay(
                 JwtClaim(FUUID.Unsafe.toUUID(uuid).asJson.noSpaces).issuedNow.expiresIn(tokenExpiration.time.toMillis)
               )
      secretKey = JwtSecretKey(jwtSecretKey.value)
      token <- jwtEncode[F](claim, secretKey, JwtAlgorithm.HS256)
    } yield token
}

object LiveTokens {

  def make[F[_]: Sync](auth: HttpAuthenticationConfig): F[Tokens[F]] =
    Sync[F]
      .delay {
        java.time.Clock.systemUTC()
      }
      .map(implicit clock => new LiveTokens[F](auth.jwtSecretKey, auth.tokenExpiration))
}
