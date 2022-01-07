package com.admios.flix

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import com.admios.flix.api.HttpApi
import com.admios.flix.authentication._
import com.admios.flix.authentication.interpreters._
import com.admios.flix.config.AppConfig
import com.admios.flix.domain.services.MovieServices
import com.admios.flix.infrastructure.interpreters.LiveServices
import com.admios.flix.infrastructure.redis.client.RedisClient
import com.admios.flix.infrastructure.repository.transactor.DbTransactor
import com.admios.flix.server.HttpServer
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.config.parser
import org.http4s.server.Server

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    HttpApp
      .create[IO]
      .use(_ => IO.never)
      .as(ExitCode.Success)
}

object HttpApp {

  def create[F[_]: ConcurrentEffect: Timer: ContextShift]: Resource[F, Server[F]] = {
    implicit val logger: Logger[F] = Slf4jLogger.getLogger[F]
    for {
      config                                       <- Resource.eval(parser.decodePathF[F, AppConfig]("app"))
      xa                                           <- DbTransactor.create(config.db)
      redis                                        <- RedisClient.make[F](config.redis)
      implicit0(services: MovieServices[F])        <- LiveServices.make[F](xa)
      implicit0(tokens: Tokens[F])                 <- Resource.eval(LiveTokens.make[F](config.http.authentication))
      implicit0(crypto: Crypto[F])                 <- Resource.eval(LiveCrypto.make(config.http.authentication))
      implicit0(users: Users[F])                   <- LiveUsers.make(xa)
      implicit0(authentication: Authentication[F]) <- LiveAuthentication.make[F](redis)(config.http.authentication)
      implicit0(usersAuth: UsersAuth[F])           <- LiveUsersAuth.make(redis)
      httpApp                                      <- HttpApi.make[F](UsersAuthMiddleware.middleware(config.http.authentication.jwtSecretKey))
      server                                       <- HttpServer.create[F](config.http.server)(httpApp.httApi).resource
    } yield server
  }
}
