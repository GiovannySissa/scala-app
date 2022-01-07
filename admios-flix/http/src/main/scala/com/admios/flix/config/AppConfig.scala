package com.admios.flix.config

import com.admios.flix.infrastructure.redis.config.RedisConfig
import com.admios.flix.infrastructure.repository.config.DataBaseSetup
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class AppConfig(http: HttpConfig, db: DataBaseSetup, redis: RedisConfig)

object AppConfig {
  implicit val appConfigDec: Decoder[AppConfig] = deriveDecoder
}
