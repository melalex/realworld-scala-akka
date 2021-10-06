package com.melalex.realworld
package users.route

import commons.auth.service.TokenService
import commons.model.Email
import commons.web.RouteProvider
import users.dto.{UserAuthenticationDto, UserRegistrationDto, UserUpdateDto}
import users.mapper.UserMapper
import users.service.UserService

import akka.http.scaladsl.server.Route
import cats.data._
import cats.implicits._
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class UserRouteProvider(
    userService: UserService[Future],
    tokenService: TokenService,
    userMapper: UserMapper
)(implicit ec: ExecutionContext)
    extends RouteProvider {

  override def provideRoute: Route = pathPrefix("user") {
    post {
      path("login") {
        entity(as[UserAuthenticationDto]) { authentication =>
          val response =
            EitherT(userService.authenticateUser(Email(authentication.user.email), authentication.user.password))
              .map(userMapper.map)

          completeEitherT(response)
        }
      } ~
        pathEndOrSingleSlash {
          entity(as[UserRegistrationDto].validate) { registration =>
            complete(userService.createUser(userMapper.map(registration)).map(userMapper.map))
          }
        }
    }
  } ~
    path("user") {
      authenticated(tokenService) { auth =>
        get {
          val response = EitherT(userService.getUserByUsername(auth.username))
            .map(userMapper.map)

          completeEitherT(response)
        } ~
          put {
            entity(as[UserUpdateDto].validate) { update =>
              val response = EitherT(userService.updateUser(auth.username, userMapper.map(update)))
                .map(userMapper.map)

              completeEitherT(response)
            }
          }
      }
    }
}
