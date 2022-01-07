package com.admios.flix.infrastructure.repository.config

import io.circe.Decoder
import io.circe.config.syntax.durationDecoder
import io.circe.generic.semiauto.deriveDecoder

import scala.concurrent.duration.FiniteDuration

final case class DBAddress(url: String) extends AnyVal
final case class DBDriver(className: String) extends AnyVal
final case class DBPassword(value: String) extends AnyVal
final case class DBUsername(value: String) extends AnyVal
final case class DBConnectionTimeout(duration: FiniteDuration) extends AnyVal
final case class DBMaxLifeTime(duration: FiniteDuration) extends AnyVal
final case class DBMaximumPoolSize(value: Int) extends AnyVal
final case class DBAwaitingConnectionPool(size: Int) extends AnyVal
final case class DBMinimumIdle(value: Int) extends AnyVal
final case class DBIdleTimeout(duration: FiniteDuration) extends AnyVal

final case class DBConnectionPool(
    connectionTimeout: DBConnectionTimeout,
    idleTimeout: DBIdleTimeout,
    maximumPoolSize: DBMaximumPoolSize,
    maxLifeTime: DBMaxLifeTime,
    minimumIdle: DBMinimumIdle,
    awaitingConnectionPool: DBAwaitingConnectionPool
)

final case class DBUser(username: DBUsername, password: DBPassword) {
  override def toString: String = "<hidden>"
}

final case class DataBaseSetup(
    address: DBAddress,
    driver: DBDriver,
    user: DBUser,
    connectionPool: DBConnectionPool
)

object DataBaseSetup {
  implicit val addressDec: Decoder[DBAddress] = deriveDecoder
  implicit val driverDec: Decoder[DBDriver] = deriveDecoder
  implicit val passwordDec: Decoder[DBPassword] = deriveDecoder
  implicit val usernameDec: Decoder[DBUsername] = deriveDecoder
  implicit val userConfDec: Decoder[DBUser] = deriveDecoder
  implicit val connectionTimeoutDec: Decoder[DBConnectionTimeout] = deriveDecoder
  implicit val idleTimeoutDec: Decoder[DBIdleTimeout] = deriveDecoder
  implicit val maximumPoolSizeDec: Decoder[DBMaximumPoolSize] = deriveDecoder
  implicit val maxLifeTimeDec: Decoder[DBMaxLifeTime] = deriveDecoder
  implicit val minimumIdleDec: Decoder[DBMinimumIdle] = deriveDecoder
  implicit val awaitingConnectionPoolDec: Decoder[DBAwaitingConnectionPool] = deriveDecoder
  implicit val dbConnectionPoolDec: Decoder[DBConnectionPool] = deriveDecoder
  implicit val dataBaseSetupDec: Decoder[DataBaseSetup] = deriveDecoder
}
