package com.admios.flix.api

import cats.effect.Sync
import cats.syntax.option._
import com.admios.flix.config.HttpServerConfig
import com.admios.flix.models.{Movie, Director}
import org.http4s.rho.RhoMiddleware
import org.http4s.rho.swagger.models._
import org.http4s.rho.swagger.{DefaultSwaggerFormats, SwaggerMetadata, SwaggerSupport}
import org.http4s.server.Router
import org.http4s.server.middleware.AutoSlash
import org.http4s.{HttpRoutes, RhoDsl}

import scala.reflect.runtime.universe.typeOf

final class MovieApiDoc[F[_]: Sync] private (swaggerSupport: SwaggerSupport[F])(serverConfig: HttpServerConfig)
    extends RhoDsl[F] {

  import swaggerSupport._

  private val middleWare: RhoMiddleware[F] = createRhoMiddleware(
    swaggerFormats = DefaultSwaggerFormats
      .withSerializers(typeOf[Director], Director.model)
      .withSerializers(typeOf[Movie], Movie.model),

    swaggerMetadata = SwaggerMetadata(
      apiInfo = Info(
        title       = "Movies API",
        version     = "1.0.0",
        description = "This is the movie documentation, this describe kind of services implemented".some
      ),
      host     = s"${serverConfig.host.ip}:${serverConfig.port.number}".some,
      basePath = "/swagger/".some,
      schemes  = List(Scheme.HTTPS),
      security = List(SecurityRequirement("Authorization", List.empty)),
      securityDefinitions = Map(
        "Authorization" -> ApiKeyAuthDefinition("Authorization", In.HEADER)
      )
    )
  )

  def api: HttpRoutes[F] =
    AutoSlash(
      Router(
        "/swagger/movie" -> MovieDoc[F](swaggerSupport).docRoutes.toRoutes(middleWare)
      )
    )

}

object MovieApiDoc {

  def apply[F[_]: Sync](swaggerSupport: SwaggerSupport[F])(serverConfig: HttpServerConfig): MovieApiDoc[F] =
    new MovieApiDoc[F](swaggerSupport)(serverConfig)
}
