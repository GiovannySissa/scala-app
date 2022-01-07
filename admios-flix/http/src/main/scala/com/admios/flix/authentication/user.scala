package com.admios.flix.authentication

import cats.data.ValidatedNel
import cats.syntax.either._
import com.admios.flix.errors.{EmptyPassword, EmptyUsername, InvalidInput}
import io.chrisdavenport.fuuid.FUUID
import mouse.boolean._

import javax.crypto.Cipher

final case class UserId(value: FUUID) extends AnyVal

final case class User private (userId: UserId, userName: UserName)

object User {
  def apply(userId: FUUID, username: String): ValidatedNel[InvalidInput, User] =
    UserName(username).toValidatedNel map (userName => new User(UserId(userId), userName))
}

final case class UserName private (value: String) extends AnyVal

object UserName {
  def apply(inputUsername: String): Either[InvalidInput, UserName] =
    inputUsername.nonEmpty.either(EmptyUsername(), new UserName(inputUsername))
}

final case class Password private (secureValue: String) extends AnyVal
object Password {
  def apply(inputPassword: String): Either[InvalidInput, Password] =
    inputPassword.nonEmpty.either(EmptyPassword(), new Password(inputPassword))
}

// ===============
final case class EncryptedPassword(value: String) extends AnyVal

final case class EncryptCipher(value: Cipher) extends AnyVal
final case class DecryptCipher(value: Cipher) extends AnyVal
