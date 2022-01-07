package com.admios.flix.config

import com.admios.flix.authentication.config.HttpAuthenticationConfig
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class HttpConfig(server: HttpServerConfig, authentication: HttpAuthenticationConfig)

final case class HttpServerConfig(host: HttpHost, port: HttpPort)
final case class HttpHost(ip: String) extends AnyVal
final case class HttpPort(number: Int) extends AnyVal



object HttpConfig {
  implicit val portDecoder: Decoder[HttpPort] = deriveDecoder
  implicit val hostDecoder: Decoder[HttpHost] = deriveDecoder
  implicit val serverDecoder: Decoder[HttpServerConfig] = deriveDecoder
  implicit val httpConfigDecoder: Decoder[HttpConfig] = deriveDecoder
}
