package com.admios.flix.api

import cats.effect.{Concurrent, Resource, Timer}
import cats.syntax.semigroupk._
import com.admios.flix.authentication.{Authentication, User}
import com.admios.flix.domain.services.MovieServices
import org.http4s.implicits._
import org.http4s.server.middleware.{AutoSlash, CORS, Timeout}
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{HttpApp, HttpRoutes}

import scala.concurrent.duration.DurationInt

final class HttpApi[F[_]: Concurrent: Timer: MovieServices: Authentication] private (
    authMiddleware: AuthMiddleware[F, User]
) {

  val authHttpServices: AuthHttpServices[F] = AuthHttpServices[F]
  val authRoutes: HttpRoutes[F] = authHttpServices.loginRoutes <+> authMiddleware(authHttpServices.logoutRoutes)

  val movieRoutes: HttpRoutes[F] = authMiddleware(MovieHttpServices[F].routes)

  private val routes: HttpRoutes[F] = Router(
    s"${Version.v1}/auth"  -> authRoutes,
    s"${Version.v1}/movie" -> movieRoutes
  )

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http = http, config = CORS.DefaultCORSConfig)
    } andThen { http: HttpRoutes[F] =>
      Timeout(40.seconds)(http)
    }
  }

  val httApi: HttpApp[F] = middleware(routes).orNotFound
}

object HttpApi {

  def apply[F[_]: Concurrent: Timer: MovieServices: Authentication](
      authMiddleware: AuthMiddleware[F, User]
  ): HttpApi[F] =
    new HttpApi[F](authMiddleware)

  def make[F[_]: Concurrent: Timer: MovieServices: Authentication](
      authMiddleware: AuthMiddleware[F, User]
  ): Resource[F, HttpApi[F]] =
    Resource.eval(
      Concurrent[F].delay(apply(authMiddleware))
    )
}
