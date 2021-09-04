package com.melalex.realworld
package commons.i18n.util

import commons.i18n.util.MessageKeys.ModelKeyPrefix
import commons.model.FieldName

import scala.reflect.runtime.universe._

trait MessageKeys {

  def modelKey[A: TypeTag] = s"$ModelKeyPrefix.${typeOf[A].getClass.getSimpleName}"

  def fieldKey[A: TypeTag](fieldName: FieldName[A]) = s"${modelKey[A]}.${fieldName.value}"
}

object MessageKeys extends MessageKeys {

  val ModelKeyPrefix = "model"
}
