package com.admios.flix

import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp, Timer}
import com.admios.flix.api.MovieApiDoc
import com.admios.flix.config.HttpConfig
import fs2.Stream
import io.circe.config.parser
import org.http4s.implicits._
import org.http4s.rho.swagger.SwaggerSupport
import org.http4s.server.blaze.BlazeServerBuilder

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    Stream
      .eval(parser.decodePathF[IO, HttpConfig]("app.http"))
      .flatMap(buildServer[IO])
      .compile
      .drain
      .as(ExitCode.Success)

  def buildServer[F[_]: ConcurrentEffect: Timer](config: HttpConfig): Stream[F, ExitCode] =
    BlazeServerBuilder[F](ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10)))
      .withHttpApp(MovieApiDoc[F](SwaggerSupport.apply[F])(config.server).api.orNotFound)
      .bindHttp(port = config.server.port.number, host = config.server.host.ip)
      .serve
}
