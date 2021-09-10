package com.melalex.realworld
package users.service.impl

import test.fixture.UserFixture
import test.spec.UnitTestSpec

class BCryptPasswordHashServiceSuite extends UnitTestSpec {

  private val passwordHashService = new BCryptPasswordHashService()

  private val invalidPassword = "invalid password"

  "verify" should "return true when valid password is provided" in {
    passwordHashService.verify(UserFixture.PlainTextPassword, passwordHashService.hash(UserFixture.PlainTextPassword)) shouldEqual true
  }

  it should "return false when invalid password is provided" in {
    passwordHashService.verify(invalidPassword, passwordHashService.hash(UserFixture.PlainTextPassword)) shouldEqual false
  }

  it should "return true for password from UserFixture" in {
    passwordHashService.verify(UserFixture.PlainTextPassword, UserFixture.Password) shouldEqual true
  }
}
