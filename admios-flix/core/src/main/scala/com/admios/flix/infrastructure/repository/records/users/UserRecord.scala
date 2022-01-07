package com.admios.flix.infrastructure.repository.records.users

import io.chrisdavenport.fuuid.FUUID

final case class UserRecord (id: FUUID, username: String, password: String)

