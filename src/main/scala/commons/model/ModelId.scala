package com.melalex.realworld
package commons.model

import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}

case class ModelId(value: Long) extends AnyVal

object ModelId {

  val UnSaved: ModelId = ModelId(-1)

  implicit val ModelIdMapping: BaseColumnType[ModelId] =
    MappedColumnType.base[ModelId, Long](
      vo => vo.value,
      id => ModelId(id)
    )
}
