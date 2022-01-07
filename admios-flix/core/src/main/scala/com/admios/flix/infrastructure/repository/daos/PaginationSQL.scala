package com.admios.flix.infrastructure.repository.daos

import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.{Read, fragment}

trait PaginationSQL {

  def paginate[R: Read](query: fragment.Fragment)(limit: Int, offset: Int): doobie.Query0[R] =
    (query ++ fr"LIMIT $limit OFFSET $offset").queryWithLogHandler(LogHandler.jdkLogHandler)
}
