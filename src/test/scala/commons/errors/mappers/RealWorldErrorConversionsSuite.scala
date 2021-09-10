package com.melalex.realworld
package commons.errors.mappers

import commons.errors.dto.RealWorldErrorDto
import commons.errors.model.RealWorldError
import commons.i18n.service.I18nService
import test.spec.UnitTestSpec

import org.mockito.IdiomaticMockito

import java.util.Locale

class RealWorldErrorConversionsSuite extends UnitTestSpec with IdiomaticMockito {

  private implicit val locale: Locale = Locale.ENGLISH

  private val i18nService = mock[I18nService]

  private val realWorldErrorConversions = new RealWorldErrorConversions(i18nService)

  "RealWorldErrorConversions" should "map error to dto" in {
    val errorMessage = "Internal Server Error"

    i18nService.getMessage(RealWorldError.InternalServerError.messageCode) returns errorMessage

    realWorldErrorConversions.toDto(RealWorldError.InternalServerError) shouldEqual RealWorldErrorDto(Seq(errorMessage))
  }
}
