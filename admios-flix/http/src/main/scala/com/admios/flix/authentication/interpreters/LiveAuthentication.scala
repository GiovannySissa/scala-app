package com.admios.flix.authentication.interpreters

import cats.effect.{Resource, Sync}
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import com.admios.flix.authentication._
import com.admios.flix.authentication.config.{HttpAuthenticationConfig, TokenExpiration}
import com.admios.flix.errors.{InvalidUserOrPassword, UserNameInUse}
import com.admios.flix.infrastructure.redis.records.UserRecord
import dev.profunktor.auth.jwt
import dev.profunktor.redis4cats.RedisCommands
import io.circe.syntax._

final class LiveAuthentication[F[_]: Sync: Tokens: Users] private (redis: RedisCommands[F, String, String])(
    tokenExpiration: TokenExpiration
) extends Authentication[F] {

  override def addUser(userName: UserName, password: Password): F[jwt.JwtToken] =
    for {
      userFound <- Users[F].find(userName, password)
      _         <- Sync[F].whenA(userFound.isDefined)(UserNameInUse.of(userName.value).raiseError)
      userId    <- Users[F].create(userName, password)
      token     <- Tokens[F].create
      _         <- storeInRedis(UserRecord(userId = userId.value, username = userName.value))(token)
    } yield token

  override def login(userName: UserName, password: Password): F[jwt.JwtToken] =
    for {
      maybeUser        <- Users[F].find(userName, password)
      user             <- maybeUser.liftTo[F](InvalidUserOrPassword())
      maybeTokenCached <- redis.get(userName.value)
      token <-
        maybeTokenCached.fold {
          Tokens[F].create.flatTap(t =>
            storeInRedis(UserRecord(userId = user.userId.value, username = userName.value))(t)
          )
        }(t => jwt.JwtToken(t).pure[F])
    } yield token

  private def storeInRedis(userRecord: UserRecord)(token: jwt.JwtToken): F[Unit] = {
    val redisUserRecord = userRecord.asJson.noSpaces
    for {
      _ <- redis.setEx(token.value, redisUserRecord, tokenExpiration.time)
      _ <- redis.setEx(userRecord.username, token.value, tokenExpiration.time)
    } yield ()
  }

  override def logout(token: jwt.JwtToken, userName: UserName): F[Unit] =
    for {
      _ <- redis.del(token.value)
      _ <- redis.del(userName.value)
    } yield ()

}

object LiveAuthentication {

  def apply[F[_]: Sync: Tokens: Users](redis: RedisCommands[F, String, String])(
      tokenExpiration: TokenExpiration
  ): Authentication[F] = new LiveAuthentication[F](redis)(tokenExpiration)

  def make[F[_]: Sync: Tokens: Users](redis: RedisCommands[F, String, String])(
      auth: HttpAuthenticationConfig
  ): Resource[F, Authentication[F]] =
    Resource.eval {
      Sync[F].pure(apply(redis)(auth.tokenExpiration))
    }

}
