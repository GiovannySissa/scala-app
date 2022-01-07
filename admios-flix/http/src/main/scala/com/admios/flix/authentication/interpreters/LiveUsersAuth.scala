package com.admios.flix.authentication.interpreters

import cats.effect.{Resource, Sync}
import cats.syntax.functor._
import com.admios.flix.authentication.config.JwtKey
import com.admios.flix.authentication.{User, UsersAuth}
import com.admios.flix.infrastructure.redis.records.UserRecord
import dev.profunktor.auth.jwt.JwtAuth
import dev.profunktor.auth.{JwtAuthMiddleware, jwt}
import dev.profunktor.redis4cats.RedisCommands
import io.circe.parser.decode
import org.http4s.server.AuthMiddleware
import pdi.jwt.{JwtAlgorithm, JwtClaim}

final class LiveUsersAuth[F[_]: Sync] private (redis: RedisCommands[F, String, String]) extends UsersAuth[F] {

  override def find(token: jwt.JwtToken)(claim: JwtClaim): F[Option[User]] =
    redis.get(token.value).map {
      _.flatMap { rawUserJson =>
        decode[UserRecord](rawUserJson)
          .flatMap(ur => User(userId = ur.userId, username = ur.username).toEither)
          .toOption
      }
    }

}

object LiveUsersAuth {

  def apply[F[_]: Sync](redis: RedisCommands[F, String, String]): UsersAuth[F] =
    new LiveUsersAuth[F](redis)

  def make[F[_]: Sync](redis: RedisCommands[F, String, String]): Resource[F, UsersAuth[F]] =
    Resource.eval {
      Sync[F].pure(apply(redis))
    }
}

object UsersAuthMiddleware {
  def middleware[F[_]: Sync: UsersAuth](jwtSecretKey: JwtKey): AuthMiddleware[F, User] = {
    val jwtAuth = JwtAuth.hmac(
      jwtSecretKey.value,
      JwtAlgorithm.HS256
    )
    JwtAuthMiddleware[F, User](jwtAuth, UsersAuth[F].find)
  }
}
