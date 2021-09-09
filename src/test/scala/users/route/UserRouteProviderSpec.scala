package com.melalex.realworld
package users.route

import commons.auth.service.TokenService
import commons.auth.web.AuthorizationHeader
import fixture.{RouteSpec, UserFixture}
import users.dto.UserDto
import users.service.UserService

import akka.http.scaladsl.model.StatusCodes
import io.circe.generic.auto._

import scala.concurrent.Future

class UserRouteProviderSpec extends RouteSpec with UserFixture {

  private val userService  = mock[UserService[Future]]
  private val tokenService = mock[TokenService]

  private val userRoute = new UserRouteProvider(userService, tokenService)(executionContext).provideRoute

  "UserRouteProvider" should "login" in {
    userService.authenticateUser(UserFixture.Email, UserFixture.PlainTextPassword) returns Future.successful(Right(userWithToken))

    Post("/users/login", httpEntity(userAuthenticationDto)) ~> userRoute ~> check {
      status shouldBe StatusCodes.OK
      responseAs[UserDto] shouldBe savedUserDto
    }
  }

  it should "register" in {
    userService.createUser(newUser) returns Future.successful(userWithToken)

    Post("/users", httpEntity(userRegistrationDto)) ~> userRoute ~> check {
      status shouldBe StatusCodes.OK
      responseAs[UserDto] shouldBe savedUserDto
    }
  }

  it should "get current user" in {
    userService.getUserById(UserFixture.Id) returns Future.successful(Right(savedUser))

    Get("/users") ~> AuthorizationHeader(UserFixture.ValidSecurityToken) ~> userRoute ~> check {
      status shouldBe StatusCodes.OK
      responseAs[UserDto] shouldBe savedUserDto
    }
  }

  it should "update user" in {
    userService.updateUser(UserFixture.Id, updateUser) returns Future.successful(Right(savedUserAfterUpdate))

    Put("/users") ~> AuthorizationHeader(UserFixture.ValidSecurityToken) ~> userRoute ~> check {
      status shouldBe StatusCodes.OK
      responseAs[UserDto] shouldBe savedUserAfterUpdateDto
    }
  }
}
