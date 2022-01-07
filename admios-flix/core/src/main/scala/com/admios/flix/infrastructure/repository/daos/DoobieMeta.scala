package com.admios.flix.infrastructure.repository.daos

import com.admios.flix.domain.movie.Genre
import doobie.util.meta.Meta

object DoobieMeta {

  implicit val genreMeta: Meta[Genre] = Meta.StringMeta.timap(Genre.withNameInsensitive)(_.value)
}
