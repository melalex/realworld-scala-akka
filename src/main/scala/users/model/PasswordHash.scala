package com.melalex.realworld
package users.model

import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

case class PasswordHash(value: String) extends AnyVal

object PasswordHash {

  implicit val PasswordHashMapping: BaseColumnType[PasswordHash] =
    MappedColumnType.base[PasswordHash, String](
      vo => vo.value,
      id => PasswordHash(id)
    )
}
