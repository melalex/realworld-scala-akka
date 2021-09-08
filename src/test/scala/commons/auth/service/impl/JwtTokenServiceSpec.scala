package com.melalex.realworld
package commons.auth.service.impl

import commons.auth.model.{SecurityToken, UserPrincipalWithToken}
import commons.errors.model.RealWorldError
import fixture.{FixedInstantProvider, PropertiesFixture, RealWorldSpec, UserFixture}

class JwtTokenServiceSpec extends RealWorldSpec with PropertiesFixture with UserFixture {

  private val instantProvider = new FixedInstantProvider()
  private val jwtTokenService = new JwtTokenService(instantProvider, realWorldProperties)

  "verifyToken" should "return UserPrincipalWithToken for valid token" in {
    val userWithToken = jwtTokenService.generateNewTokenForUser(savedUser)

    jwtTokenService.validateToken(userWithToken.token) shouldBe Right(UserPrincipalWithToken(actualUserPrincipal, userWithToken.token))
  }

  it should "return CredentialsException for invalid token" in {
    val errors = jwtTokenService.validateToken(SecurityToken("invalid token")).left.map(_.errors)

    errors shouldBe Left(Seq(RealWorldError.InvalidToken))
  }

  "generateNewToken" should "return SecurityToken" in {
    jwtTokenService.generateNewToken(actualUserPrincipal) shouldBe UserFixture.ValidSecurityToken
  }
}
