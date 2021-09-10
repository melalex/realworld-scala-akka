package com.melalex.realworld
package users.route

import commons.auth.service.TokenService
import commons.auth.web.RealWorldSecurityDirectives
import commons.validation.web.ValidationDirectives
import commons.web.{RealWorldDirectives, RouteProvider}
import users.dto.{UserAuthenticationDto, UserRegistrationDto, UserUpdateDto}
import users.mapper.UserConversions
import users.service.UserService

import akka.http.scaladsl.server.directives.FutureDirectives
import akka.http.scaladsl.server.{Directives, Route}
import cats.data._
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class UserRouteProvider(
    userService: UserService[Future],
    tokenService: TokenService
)(implicit ec: ExecutionContext)
    extends RouteProvider
    with FailFastCirceSupport
    with Directives
    with FutureDirectives
    with RealWorldDirectives
    with RealWorldSecurityDirectives
    with ValidationDirectives {

  override def provideRoute: Route = pathPrefix("users") {
    post {
      path("login") {
        entity(as[UserAuthenticationDto]) { authentication =>
          val response = EitherT(userService.authenticateUser(authentication.user.email, authentication.user.password))
            .map(UserConversions.toUserDto)

          completeEitherT(response)
        }
      } ~
        pathEndOrSingleSlash {
          entity(as[UserRegistrationDto].validate) { registration =>
            complete(userService.createUser(UserConversions.toNewUser(registration)).map(UserConversions.toUserDto))
          }
        }
    } ~
      authenticated(tokenService) { auth =>
        pathEndOrSingleSlash {
          get {
            val response = EitherT(userService.getUserById(auth.principal.id))
              .map(_.withToken(auth.token))
              .map(UserConversions.toUserDto)

            completeEitherT(response)
          } ~
            put {
              entity(as[UserUpdateDto].validate) { update =>
                val response = EitherT(userService.updateUser(auth.principal.id, UserConversions.toUserUpdate(update)))
                  .map(_.withToken(auth.token))
                  .map(UserConversions.toUserDto)

                completeEitherT(response)
              }
            }
        }
      }
  }
}
