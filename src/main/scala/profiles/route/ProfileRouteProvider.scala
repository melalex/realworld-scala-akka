package com.melalex.realworld
package profiles.route

import commons.auth.service.TokenService
import commons.web.RouteProvider
import profiles.mapper.ProfileMapper
import profiles.service.ProfileService

import akka.http.scaladsl.server.Route
import cats.data.EitherT
import cats.implicits._
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class ProfileRouteProvider(
    tokenService: TokenService,
    profileService: ProfileService[Future],
    profileMapper: ProfileMapper
)(implicit ec: ExecutionContext)
    extends RouteProvider {

  override def provideRoute: Route = pathPrefix("profiles" / usernameMatcher) { username =>
    get {
      maybeAuthenticated(tokenService) { implicit auth =>
        val response = EitherT(profileService.getByUsername(username))
          .map(profileMapper.map)

        completeEitherT(response)
      }
    }
    path("follow") {
      authenticated(tokenService) { implicit auth =>
        post {
          val response = EitherT(profileService.follow(username))
            .map(profileMapper.map)

          completeEitherT(response)
        }
        delete {
          val response = EitherT(profileService.unfollow(username))
            .map(profileMapper.map)

          completeEitherT(response)
        }
      }
    }
  }
}
