package com.admios.flix.authentication

import dev.profunktor.auth.jwt.JwtToken

trait Tokens[F[_]] {
  def create: F[JwtToken]
}

object Tokens {

  def apply[F[_]: Tokens]: Tokens[F] = implicitly
}
