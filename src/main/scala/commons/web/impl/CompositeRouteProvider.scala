package com.melalex.realworld
package commons.web.impl

import commons.errors.mappers.RealWorldErrorConversions
import commons.errors.model._
import commons.i18n.web.I18nDirectives
import commons.web.RouteProvider
import config.RealWorldProperties

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.typesafe.scalalogging.Logger
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import java.util.Locale

class CompositeRouteProvider(
    delegates: Set[RouteProvider],
    errorConversions: RealWorldErrorConversions,
    realWorldProperties: RealWorldProperties
) extends I18nDirectives
    with FailFastCirceSupport
    with Directives {

  private val logger = Logger[CompositeRouteProvider]

  def provideRoute: Route =
    locale(realWorldProperties.supportedLocales) { implicit locale =>
      handleExceptions(exceptionHandler) {
        delegates
          .map(_.provideRoute)
          .reduce((r1, r2) => r1 ~ r2)
      }
    }

  private def exceptionHandler(implicit locale: Locale): ExceptionHandler = ExceptionHandler {
    case NotFoundException(errors, _)    => complete(NotFound, errorConversions.toDto(errors))
    case CredentialsException(errors, _) => complete(Unauthorized, errorConversions.toDto(errors))
    case ClientException(errors, _)      => complete(BadRequest, errorConversions.toDto(errors))
    case ServerException(errors, _)      => complete(InternalServerError, errorConversions.toDto(errors))

    case ex =>
      logger.error("Handled exception", ex)
      complete(InternalServerError, errorConversions.toDto(RealWorldError.InternalServerError))
  }
}
