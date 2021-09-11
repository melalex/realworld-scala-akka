package com.melalex.realworld
package profiles.route

import commons.auth.service.TokenService
import commons.web.RouteProvider

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

class ProfileRouteProvider(tokenService: TokenService) extends RouteProvider {

  override def provideRoute: Route = pathPrefix("profiles" / Segment) { username =>
    get {
      maybeAuthenticated(tokenService) { auth =>
        complete(StatusCodes.OK)
      }
    }
    path("follow") {
      authenticated(tokenService) { auth =>
        post {
          complete(StatusCodes.OK)
        }
        delete {
          complete(StatusCodes.OK)
        }
      }
    }
  }
}
