package com.melalex.realworld
package articles.model

import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

case class Slug(value: String) extends AnyVal

object Slug {

  implicit val ModelIdMapping: BaseColumnType[Slug] =
    MappedColumnType.base[Slug, String](
      vo => vo.value,
      id => Slug(id)
    )
}
