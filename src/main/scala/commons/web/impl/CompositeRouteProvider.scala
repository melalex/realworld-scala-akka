package com.melalex.realworld
package commons.web.impl

import commons.errors.mappers.RealWorldErrorConversions
import commons.errors.model.RealWorldError
import commons.errors.{ClientException, NotFoundException, SecurityException, ServerException}
import commons.i18n.route.I18nDirectives
import commons.web.RouteProvider
import config.RealWorldProperties

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import java.util.Locale

class CompositeRouteProvider(
    delegates: Set[RouteProvider],
    errorConversions: RealWorldErrorConversions,
    realWorldProperties: RealWorldProperties
) extends RouteProvider
    with I18nDirectives
    with FailFastCirceSupport
    with Directives {

  def provideRoute: Route =
    locale(realWorldProperties.supportedLocales) { implicit locale =>
      handleExceptions(exceptionHandler) {
        delegates
          .map(_.provideRoute)
          .reduce((r1, r2) => r1 ~ r2)
      }
    }

  private def exceptionHandler(implicit locale: Locale): ExceptionHandler = ExceptionHandler {
    case NotFoundException(errors) => complete(NotFound, errorConversions.toDto(errors))
    case SecurityException(errors) => complete(Unauthorized, errorConversions.toDto(errors))
    case ClientException(errors)   => complete(BadRequest, errorConversions.toDto(errors))
    case ServerException(errors)   => complete(InternalServerError, errorConversions.toDto(errors))
    case _                         => complete(InternalServerError, errorConversions.toDto(RealWorldError.InternalServerError))
  }
}
