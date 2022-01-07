package com.admios.flix.infrastructure.repository.transactor

import cats.effect.{Async, Blocker, ContextShift, Resource}
import com.admios.flix.infrastructure.repository.config.DataBaseSetup
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

object DbTransactor {

  def create[F[_]: Async: ContextShift](dataBaseSetup: DataBaseSetup): Resource[F, HikariTransactor[F]] =
    for {
      dataSource    <- buildDataSource(dataBaseSetup)
      connectEC     <- ExecutionContexts.fixedThreadPool[F](dataBaseSetup.connectionPool.awaitingConnectionPool.size)
      blockingOpsEC <- ExecutionContexts.cachedThreadPool[F].map(Blocker.liftExecutionContext)
      transactor    <- HikariTransactor.fromHikariConfig(dataSource, connectEC, blockingOpsEC)
    } yield transactor

  def buildDataSource[F[_]: Async](dbSetup: DataBaseSetup): Resource[F, HikariDataSource] =
    Resource.eval(
      Async[F].delay {
        val config = new HikariConfig
        val connectionPool = dbSetup.connectionPool

        config setJdbcUrl dbSetup.address.url
        config setUsername dbSetup.user.username.value
        config setPassword dbSetup.user.password.value
        config setDriverClassName dbSetup.driver.className
        config setConnectionTimeout connectionPool.connectionTimeout.duration.toMillis
        config setIdleTimeout connectionPool.idleTimeout.duration.toMillis
        config setMaxLifetime connectionPool.maxLifeTime.duration.toMillis
        config setMaximumPoolSize connectionPool.maximumPoolSize.value
        config setMinimumIdle connectionPool.minimumIdle.value
        new HikariDataSource(config)
      }
    )
}
