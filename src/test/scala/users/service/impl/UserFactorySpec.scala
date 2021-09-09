package com.melalex.realworld
package users.service.impl

import commons.util.InstantProvider
import fixture.{FixedInstantProvider, RealWorldSpec, UserFixture}
import users.service.{PasswordHashService, UserFactory}

import org.mockito.IdiomaticMockito

import java.time.Duration

class UserFactorySpec extends RealWorldSpec with UserFixture with IdiomaticMockito {

  private val instantProvider     = mock[InstantProvider]
  private val passwordHashService = mock[PasswordHashService]

  private val userFactory = new UserFactory(instantProvider, passwordHashService)

  "UserFactory" should "create new unsaved user" in {
    instantProvider.provide() returns FixedInstantProvider.Now
    passwordHashService.hash(UserFixture.PlainTextPassword) returns UserFixture.Password

    userFactory.createNewUser(newUser) shouldBe unsavedUser
  }

  it should "create updated user" in {
    val updateInstant = FixedInstantProvider.Now.plus(Duration.ofHours(1))

    instantProvider.provide() returns updateInstant

    userFactory.createUpdatedUser(savedUser, updateUser) shouldBe savedUserAfterUpdate.copy(updatedAt = updateInstant)
  }
}
