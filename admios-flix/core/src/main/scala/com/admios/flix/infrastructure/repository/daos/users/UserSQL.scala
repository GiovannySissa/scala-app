package com.admios.flix.infrastructure.repository.daos.users

import com.admios.flix.infrastructure.repository.records.users.UserRecord
import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.query.Query0
import doobie.util.update.Update0
import io.chrisdavenport.fuuid.doobie.implicits._
import doobie.postgres.implicits._


object UserSQL {

  def create(record: UserRecord): Update0 =
    sql"""INSERT INTO  USERS (ID, USERNAME, PASSWORD)
         |          VALUES (${record.id}, ${record.username}, ${record.password})""".stripMargin
    .updateWithLogHandler(LogHandler.jdkLogHandler)

  def findByName(username: String): Query0[UserRecord] =
    sql"""SELECT ID, USERNAME, PASSWORD FROM USERS
         WHERE USERNAME = $username
       """.queryWithLogHandler[UserRecord](LogHandler.jdkLogHandler)

}
