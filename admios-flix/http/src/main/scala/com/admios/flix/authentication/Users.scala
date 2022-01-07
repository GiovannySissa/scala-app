package com.admios.flix.authentication

trait Users[F[_]] {

  def find(userName: UserName, password: Password): F[Option[User]]
  def create(userName: UserName, password: Password): F[UserId]

}

object Users {

  def apply[F[_]: Users]: Users[F] = implicitly
}
