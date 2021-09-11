package com.melalex.realworld
package comments.route

import commons.auth.service.TokenService
import commons.web.RouteProvider

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

class CommentRoute(tokenService: TokenService) extends RouteProvider {

  override def provideRoute: Route = pathPrefix("articles" / Segment / "comments") { slug =>
    pathEnd {
      authenticated(tokenService) { auth =>
        post {
          complete(StatusCodes.OK)
        }
      }
      maybeAuthenticated(tokenService) { auth =>
        get {
          complete(StatusCodes.OK)
        }
      }
    }
    path(LongNumber) { id =>
      authenticated(tokenService) { auth =>
        delete {
          complete(StatusCodes.OK)
        }
      }
    }
  }
}
