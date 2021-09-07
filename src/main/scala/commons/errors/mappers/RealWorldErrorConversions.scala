package com.melalex.realworld
package commons.errors.mappers

import commons.errors.dto.RealWorldErrorDto
import commons.errors.model.RealWorldError
import commons.i18n.service.I18nService

import java.util.Locale

class RealWorldErrorConversions(
    i18nService: I18nService
) {

  def toDto(source: RealWorldError)(implicit locale: Locale): RealWorldErrorDto = toDto(Seq(source))

  def toDto(source: Seq[RealWorldError])(implicit locale: Locale): RealWorldErrorDto = {
    val errors = source
      .map(it => i18nService.getMessage(it.messageCode, it.arguments: _*))

    RealWorldErrorDto(errors)
  }
}
