package com.admios.flix.infrastructure.repository.daos

import com.admios.flix.infrastructure.repository.records.DirectorRecord
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.log.LogHandler
import doobie.util.update.Update0
import io.chrisdavenport.fuuid.doobie.implicits._

object DirectorSQL {

  def insert(record: DirectorRecord): Update0 =
    sql"""INSERT INTO DIRECTOR(ID, NAME) VALUES (${record.id}, ${record.name})
         |         ON CONFLICT DO NOTHING""".stripMargin.updateWithLogHandler(LogHandler.jdkLogHandler)

}
