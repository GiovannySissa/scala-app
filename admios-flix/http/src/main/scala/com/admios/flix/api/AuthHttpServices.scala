package com.admios.flix.api

import cats.effect.Sync
import cats.instances.option._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.foldable._
import cats.syntax.functor._
import com.admios.flix.authentication.{Authentication, Password, User, UserName}
import com.admios.flix.dtos.auth.UserCredentials
import dev.profunktor.auth.AuthHeaders
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, HttpRoutes}

final class AuthHttpServices[F[_]: Sync: Authentication] private extends Http4sDsl[F] {

  // todo mapping errors to set correct http status
  val loginRoutes: HttpRoutes[F] = HttpRoutes.of {
    case req @ POST -> Root / "users" =>
      req.decode[UserCredentials] { dto =>
        for {
          username  <- UserName(dto.username).liftTo[F]
          password  <- Password(dto.password).liftTo[F]
          userToken <- Authentication[F].addUser(username, password)
          response  <- Created(userToken.value)
        } yield response
      }
    case req @ POST -> Root / "login" =>
      req.decode[UserCredentials] { dto =>
        for {
          username  <- UserName(dto.username).liftTo[F]
          password  <- Password(dto.password).liftTo[F]
          userToken <- Authentication[F].login(username, password)
          response  <- Ok(userToken.value)
        } yield response
      }
  }

  val logoutRoutes: AuthedRoutes[User, F] = AuthedRoutes.of { case ar @ POST -> Root / "logout" as user =>
    AuthHeaders
      .getBearerToken(ar.req)
      .traverse_ { t =>
        Authentication[F].logout(t, user.userName)
      }
      .flatMap(_ => NoContent())

  }
}

object AuthHttpServices {

  def apply[F[_]: Sync: Authentication]: AuthHttpServices[F] = new AuthHttpServices[F]
}
