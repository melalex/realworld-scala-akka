package com.melalex.realworld
package articles.route

import articles.model.ArticleParameters
import commons.auth.service.TokenService
import commons.model.Pageable
import commons.web.RouteProvider

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

class ArticleRouteProvider(tokenService: TokenService) extends RouteProvider {

  override def provideRoute: Route = pathPrefix("articles") {
    pathEnd {
      maybeAuthenticated(tokenService) { auth =>
        get {
          parameters(
            "tag".optional,
            "author".optional,
            "favorited".as[Boolean].withDefault(false),
            "limit".as[Long].withDefault(20),
            "offset".as[Long].withDefault(0)
          ).as(ArticleParameters.apply _) { params =>
            complete(StatusCodes.OK)
          }
        }
      }
      authenticated(tokenService) { auth =>
        post {
          complete(StatusCodes.OK)
        }
      }
    }
    path("feed") {
      authenticated(tokenService) { auth =>
        get {
          parameters(
            "limit".as[Long].withDefault(20),
            "offset".as[Long].withDefault(0)
          ).as(Pageable.apply _) { pageable =>
            complete(StatusCodes.OK)
          }
        }
      }
    }
    pathPrefix(Segment) { slug =>
      pathEnd {
        get {
          complete(StatusCodes.OK)
        }
      }
      authenticated(tokenService) { auth =>
        pathEnd {
          put {
            complete(StatusCodes.OK)
          }
          delete {
            complete(StatusCodes.OK)
          }
        }
        path("favorite") {
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
}
