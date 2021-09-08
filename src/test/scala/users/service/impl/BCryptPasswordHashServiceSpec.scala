package com.melalex.realworld
package users.service.impl

import fixture.{RealWorldSpec, UserFixture}

class BCryptPasswordHashServiceSpec extends RealWorldSpec {

  private val passwordHashService = new BCryptPasswordHashService()

  private val invalidPassword = "invalid password"

  "verify" should "return true when valid password is provided" in {
    passwordHashService.verify(UserFixture.PlainTextPassword, passwordHashService.hash(UserFixture.PlainTextPassword)) shouldBe true
  }

  it should "return false when invalid password is provided" in {
    passwordHashService.verify(invalidPassword, passwordHashService.hash(UserFixture.PlainTextPassword)) shouldBe false
  }

  it should "return true for password from UserFixture" in {
    passwordHashService.verify(UserFixture.PlainTextPassword, UserFixture.Password) shouldBe true
  }
}
