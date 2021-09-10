package com.melalex.realworld
package users.service.impl

import test.fixture.UserFixture
import test.spec.UnitTestSpec
import test.util.TimeSupport
import users.service.{PasswordHashService, UserFactory}

import org.mockito.IdiomaticMockito

import java.time.{Clock, Duration}

class UserFactorySuite extends UnitTestSpec with UserFixture with IdiomaticMockito {

  private val clock               = mock[Clock]
  private val passwordHashService = mock[PasswordHashService]

  private val userFactory = new UserFactory(clock, passwordHashService)

  "UserFactory" should "create new unsaved user" in {
    clock.instant() returns TimeSupport.Now
    passwordHashService.hash(UserFixture.PlainTextPassword) returns UserFixture.Password

    userFactory.createNewUser(newUser) shouldEqual unsavedUser
  }

  it should "create updated user" in {
    val updateInstant = TimeSupport.Now.plus(Duration.ofHours(1))

    clock.instant() returns updateInstant

    userFactory.createUpdatedUser(savedUser, updateUser) shouldEqual savedUserAfterUpdate.copy(updatedAt = updateInstant)
  }
}
