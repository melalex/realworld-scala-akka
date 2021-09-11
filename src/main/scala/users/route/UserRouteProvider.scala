package com.melalex.realworld
package users.route

import commons.auth.service.TokenService
import commons.web.RouteProvider
import users.dto.{UserAuthenticationDto, UserRegistrationDto, UserUpdateDto}
import users.mapper.UserConversions
import users.service.UserService

import akka.http.scaladsl.server.Route
import cats.data._
import cats.implicits._
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class UserRouteProvider(
    userService: UserService[Future],
    tokenService: TokenService
)(implicit ec: ExecutionContext)
    extends RouteProvider {

  override def provideRoute: Route =
    pathPrefix("user") {
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
      }
    } ~
      path("user") {
        authenticated(tokenService) { auth =>
          get {
            val response = EitherT(userService.getUserById(auth.id))
              .map(UserConversions.toUserDto)

            completeEitherT(response)
          } ~
            put {
              entity(as[UserUpdateDto].validate) { update =>
                val response = EitherT(userService.updateUser(auth.id, UserConversions.toUserUpdate(update)))
                  .map(UserConversions.toUserDto)

                completeEitherT(response)
              }
            }
        }
      }
}
