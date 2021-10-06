package com.melalex.realworld
package users.route

import commons.auth.service.TokenService
import test.fixture.UserFixture
import test.spec.RouteSpec
import users.dto.UserDto
import users.mapper.UserMapper
import users.service.UserService

import akka.http.scaladsl.model.StatusCodes
import io.circe.generic.auto._

import scala.concurrent.Future

class UserRouteProviderSuite extends RouteSpec with UserFixture {

  private val userService  = mock[UserService[Future]]
  private val tokenService = mock[TokenService]
  private val userMapper   = new UserMapper

  private val userRoute = new UserRouteProvider(userService, tokenService, userMapper)(executionContext).provideRoute

  "UserRouteProvider" should "login" in {
    userService
      .authenticateUser(UserFixture.Email, UserFixture.PlainTextPassword)
      .returns(Future.successful(Right(userWithToken)))

    Post("/users/login", httpEntity(userAuthenticationDto)) ~> userRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[UserDto] shouldEqual savedUserDto
    }
  }

  it should "register" in {
    userService.createUser(newUser).returns(Future.successful(userWithToken))

    Post("/users", httpEntity(userRegistrationDto)) ~> userRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[UserDto] shouldEqual savedUserDto
    }
  }

  it should "get current user" in {
    userService.getUserByUsername(UserFixture.Username).returns(Future.successful(Right(userWithToken)))
    tokenService.validateToken(UserFixture.ValidSecurityToken).returns(Right(actualUserPrincipal))

    Get("/user") ~> authorization(UserFixture.ValidSecurityToken) ~> userRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[UserDto] shouldEqual savedUserDto
    }
  }

  it should "update user" in {
    userService.updateUser(UserFixture.Username, updateUser).returns(Future.successful(Right(userWithTokenAfterUpdate)))
    tokenService.validateToken(UserFixture.ValidSecurityToken).returns(Right(actualUserPrincipal))

    Put("/user", httpEntity(userUpdateDto)) ~> authorization(UserFixture.ValidSecurityToken) ~> userRoute ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[UserDto] shouldEqual savedUserAfterUpdateDto
    }
  }
}
