package com.melalex.realworld
package profiles.route

import commons.auth.service.TokenService
import commons.web.RouteProvider
import profiles.mapper.ProfileConversions
import profiles.service.ProfileService

import akka.http.scaladsl.server.Route
import cats.data.EitherT
import cats.implicits._
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class ProfileRouteProvider(
    tokenService: TokenService,
    profileService: ProfileService[Future]
)(implicit ec: ExecutionContext)
    extends RouteProvider {

  override def provideRoute: Route = pathPrefix("profiles" / Segment) { username =>
    get {
      maybeAuthenticated(tokenService) { auth =>
        val response = EitherT(profileService.getByUsername(auth.id, username))
          .map(ProfileConversions.toDto)

        completeEitherT(response)
      }
    }
    path("follow") {
      authenticated(tokenService) { auth =>
        post {
          val response = EitherT(profileService.follow(auth.id, username))
            .map(ProfileConversions.toDto)

          completeEitherT(response)
        }
        delete {
          val response = EitherT(profileService.unfollow(auth.id, username))
            .map(ProfileConversions.toDto)

          completeEitherT(response)
        }
      }
    }
  }
}
