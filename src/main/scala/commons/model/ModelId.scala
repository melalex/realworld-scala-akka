package com.melalex.realworld
package commons.model

import slick.jdbc.MySQLProfile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _}

case class ModelId(value: Long) extends AnyVal

object ModelId {

  val UnSaved: ModelId = ModelId(0)
}
