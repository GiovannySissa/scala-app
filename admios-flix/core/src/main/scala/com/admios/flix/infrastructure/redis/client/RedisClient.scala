package com.admios.flix.infrastructure.redis.client

import cats.effect.{Concurrent, ContextShift, Resource}
import com.admios.flix.infrastructure.redis.config.RedisConfig
import dev.profunktor.redis4cats.log4cats._
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import io.chrisdavenport.log4cats.Logger

object RedisClient {

  def make[F[_]: Concurrent: ContextShift: Logger](config: RedisConfig): Resource[F, RedisCommands[F, String, String]] =
    Redis[F].utf8(config.uri.value)

}
