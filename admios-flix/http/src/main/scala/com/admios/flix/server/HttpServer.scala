package com.admios.flix.server

import cats.effect.{ConcurrentEffect, Timer}
import com.admios.flix.config.HttpServerConfig
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

object HttpServer {

  def create[F[_]: ConcurrentEffect: Timer](config: HttpServerConfig)(httpApp: HttpApp[F]): BlazeServerBuilder[F] =
    // just for complete the test should not use global
  // ExecutionContext in production
    BlazeServerBuilder[F](ExecutionContext.global)
      .bindHttp(
        port = config.port.number,
        host = config.host.ip
      )
      .withHttpApp(httpApp)

}
