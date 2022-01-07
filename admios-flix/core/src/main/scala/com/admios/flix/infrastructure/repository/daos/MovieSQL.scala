package com.admios.flix.infrastructure.repository.daos

import cats.data.NonEmptyList
import com.admios.flix.domain.movie.Genre
import com.admios.flix.infrastructure.repository.daos.DoobieMeta._
import com.admios.flix.infrastructure.repository.records.MovieRecord
import doobie.Fragments
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment
import doobie.util.log.LogHandler
import doobie.util.query.Query0
import doobie.util.update.Update0
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._

object MovieSQL extends PaginationSQL {

  def insert(record: MovieRecord): Update0 =
    sql"""|INSERT INTO MOVIE (ID, TITLE, GENRE, DIRECTOR_ID, RELEASE_DATE)
          |VALUES (${record.id}, ${record.title}, ${record.genre}, ${record.director.id}, ${record.releaseDate} )
         """.stripMargin
      .updateWithLogHandler(LogHandler.jdkLogHandler)

  def update(record: MovieRecord): Update0 =
    sql"""|INSERT INTO MOVIE (ID, TITLE, GENRE, DIRECTOR_ID, RELEASE_DATE)
          |VALUES (${record.id}, ${record.title}, ${record.genre}, ${record.director.id}, ${record.releaseDate} )
          |ON CONFLICT ON CONSTRAINT MOVIE_PK
          |DO
          |UPDATE SET
          |TITLE = ${record.title},
          |GENRE = ${record.genre},
          |DIRECTOR_ID = ${record.director.id},
          |RELEASE_DATE = ${record.releaseDate}
         """.stripMargin
      .updateWithLogHandler(LogHandler.jdkLogHandler)

  def delete(recordId: FUUID): Update0 =
    sql"""DELETE FROM MOVIE WHERE ID = $recordId"""
      .updateWithLogHandler(LogHandler.jdkLogHandler)

  def findByGenre(maybeGenres: List[Genre])(limit: Int, offset: Int): Query0[MovieRecord] = {
    val query = sql"""|SELECT M.ID, M.TITLE, M.GENRE, D.ID, D.NAME, M.RELEASE_DATE  FROM MOVIE M
                      |LEFT JOIN DIRECTOR D ON D.ID = M.DIRECTOR_ID
                      |""".stripMargin
    val applyFilter = NonEmptyList.fromList(maybeGenres) match {
      case Some(genres) => fr"WHERE " ++ Fragments.in(fr"GENRE", genres).stripMargin
      case None => Fragment.empty
    }
    paginate[MovieRecord](query ++ applyFilter)(limit, offset)
  }
}
