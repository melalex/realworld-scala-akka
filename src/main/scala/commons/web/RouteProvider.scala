package com.melalex.realworld
package commons.web

import commons.auth.web.TokenAuthorizationDirectives
import commons.validation.web.ValidationDirectives

import akka.http.scaladsl.server.directives.FutureDirectives
import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

trait RouteProvider
    extends Directives
    with FailFastCirceSupport
    with FutureDirectives
    with RealWorldDirectives
    with TokenAuthorizationDirectives
    with ValidationDirectives {

  def provideRoute: Route
}
