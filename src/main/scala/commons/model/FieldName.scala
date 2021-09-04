package com.melalex.realworld
package commons.model

import commons.i18n.util.MessageKeys

import scala.reflect.runtime.universe._

case class FieldName[A: TypeTag](value: String) extends AnyVal

object FieldName extends MessageKeys {

  implicit class FieldNameOps[A: TypeTag](value: FieldName[A]) {

    def messageKey: String = fieldKey(value)
  }
}
