package com.melalex.realworld
package feature

import test.fixture.UserFixture
import test.spec.IntegrationTestSpec
import users.dto.UserDto

import akka.http.scaladsl.model.StatusCodes
import io.circe.generic.auto._

class UserFeatures extends IntegrationTestSpec with UserFixture {

  info("As a client")
  info("I want to be able to register new user")
  info("So I will be able to login and get current user")

  Feature("User registration") {
    Scenario("Successfully register new user") {
      Given("Anonymous user")
      When("Client calls POST '/users' with valid registration form data")
      Post("/api/users", httpEntity(userRegistrationDto)) ~!> routes ~> isOk

      Then("Client able to to login")
      val authenticatedUser = Post("/api/users/login", httpEntity(userAuthenticationDto)) ~!> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[UserDto]
      }

      And("Get current user")
      Get("/api/users").withHeaders(authorization(authenticatedUser.user.token)) ~!> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[UserDto] should equal(savedUserDto)(after being unTokenized)
      }
    }
  }

  info("As an authenticated client")
  info("I want to be able to edit my user")
  info("So I will be able to update my user info")

  Feature("User info update") {
    Scenario("Successfully update user info") {
      Given("An authenticated client")
      whenReady(dbInterpreter.executeTransitionally(userRepository.save(unsavedUser))) { _ =>
        val authenticatedUser = Post("/api/users/login", httpEntity(userAuthenticationDto)) ~!> routes ~> check {
          status shouldEqual StatusCodes.OK
          responseAs[UserDto]
        }

        When("Update user")
        val updateRequest = Put("/api/users", httpEntity(userUpdateDto)).withHeaders(authorization(authenticatedUser.user.token))
        val userAfterUpdate = updateRequest ~!> routes ~> check {
          status shouldEqual StatusCodes.OK
          responseAs[UserDto]
        }

        Then("Current user info equals to updated one")
        Get("/api/users").withHeaders(authorization(userAfterUpdate.user.token)) ~!> routes ~> check {
          status shouldEqual StatusCodes.OK
          responseAs[UserDto] should equal(savedUserAfterUpdateDto)(after being unTokenized)
        }
      }
    }
  }
}
