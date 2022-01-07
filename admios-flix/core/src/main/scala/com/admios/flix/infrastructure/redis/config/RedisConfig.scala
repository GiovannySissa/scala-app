package com.admios.flix.infrastructure.redis.config

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class RedisURI(value: String) extends AnyVal
final case class RedisConfig(uri: RedisURI)

object RedisConfig {

  implicit val redisConfigDec: Decoder[RedisConfig] = deriveDecoder
  implicit val redisURIDec: Decoder[RedisURI] = deriveDecoder
}
