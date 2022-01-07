package com.admios.flix.authentication.interpreters

import cats.effect.{Resource, Sync}
import cats.syntax.eq._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.admios.flix.authentication._
import com.admios.flix.infrastructure.repository.daos.users.UserSQL
import com.admios.flix.infrastructure.repository.records.users.UserRecord
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import mouse.boolean._

final class LiveUsers[F[_]: Sync: Crypto] private (xa: Transactor[F]) extends Users[F] {

  override def find(userName: UserName, password: Password): F[Option[User]] =
    UserSQL
      .findByName(userName.value)
      .option
      .transact(xa)
      .map {
        _.flatMap { userFound =>
          (userFound.password === password.secureValue).fold(
            User(userId = userFound.id, username = userFound.username).toOption,
            Option.empty[User]
          )
        }
      }

  override def create(userName: UserName, password: Password): F[UserId] =
    for {
      id             <- FUUID.randomFUUID[F]
      securePassword <- Crypto[F].encrypt(password)
      record = UserRecord(id = id, username = userName.value, password = securePassword.value)
      _ <- UserSQL.create(record).run.transact(xa)
    } yield UserId(record.id)

}

object LiveUsers {

  def apply[F[_]: Sync: Crypto](xa: Transactor[F]): Users[F] = new LiveUsers[F](xa)

  def make[F[_]: Sync: Crypto](xa: Transactor[F]): Resource[F, Users[F]] =
    Resource.eval {
      Sync[F].pure(apply(xa))
    }
}
