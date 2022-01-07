package com.admios.flix.authentication.interpreters

import cats.effect.Sync
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.admios.flix.authentication._
import com.admios.flix.authentication.config.{HttpAuthenticationConfig, PasswordSalt}

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.spec.{IvParameterSpec, PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, SecretKeyFactory}

final class LiveCrypto[F[_]: Sync] private (encryptCipher: EncryptCipher, decryptCipher: DecryptCipher)
    extends Crypto[F] {

  override def encrypt(password: Password): F[EncryptedPassword] =
    Sync[F].delay {
      val base64 = Base64.getEncoder
      val bytes = password.secureValue.getBytes("UTF-8")
      val r = new String(base64.encode(encryptCipher.value.doFinal(bytes)), "UTF-8")
      EncryptedPassword(r)
    }

  override def decrypt(secure: EncryptedPassword): F[Password] =
    Sync[F]
      .delay {
        val base64 = Base64.getDecoder
        val bytes = secure.value.getBytes("UTF-8")
        new String(base64.decode(decryptCipher.value.doFinal(bytes)), "UTF-8")
      }
      .flatMap { Password(_).liftTo[F] }
}

object LiveCrypto {

  def make[F[_]: Sync](auth: HttpAuthenticationConfig): F[Crypto[F]] =
    Sync[F]
      .delay {
        val random = new SecureRandom()
        val ivBytes = new Array[Byte](16)
        random.nextBytes(ivBytes)
        val iv = new IvParameterSpec(ivBytes)
        val salt = auth.salt.value.getBytes("UTF-8")
        val keySpec = new PBEKeySpec("password".toCharArray, salt, 65536, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val bytes = factory.generateSecret(keySpec).getEncoded
        val sKeySpec = new SecretKeySpec(bytes, "AES")
        val eCipher = EncryptCipher(Cipher.getInstance("AES/CBC/PKCS5Padding"))
        val dCipher = DecryptCipher(Cipher.getInstance("AES/CBC/PKCS5Padding"))

        eCipher.value.init(Cipher.ENCRYPT_MODE, sKeySpec, iv)
        dCipher.value.init(Cipher.DECRYPT_MODE, sKeySpec, iv)
        (eCipher, dCipher)

      }
      .map { case (ec, dc) =>
        new LiveCrypto[F](encryptCipher = ec, decryptCipher = dc)
      }
}
