package com.admios.flix.authentication

import dev.profunktor.auth.jwt.JwtToken
import pdi.jwt.JwtClaim

trait Authentication[F[_]] {
  def addUser(userName: UserName, password: Password): F[JwtToken]
  def login(userName: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, userName: UserName): F[Unit]
}

object Authentication {
  def apply[F[_]: Authentication]: Authentication[F] = implicitly
}

trait UsersAuth[F[_]] {
  def find(token: JwtToken)(claim: JwtClaim): F[Option[User]]

}

object UsersAuth {
  def apply[F[_]: UsersAuth]: UsersAuth[F] = implicitly

}
