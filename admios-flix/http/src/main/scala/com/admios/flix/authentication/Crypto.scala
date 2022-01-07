package com.admios.flix.authentication

trait Crypto[F[_]] {

  def encrypt(password: Password): F[EncryptedPassword]
  def decrypt(secure: EncryptedPassword): F[Password]
}

object Crypto {

  def apply[F[_]: Crypto]: Crypto[F] = implicitly

}
