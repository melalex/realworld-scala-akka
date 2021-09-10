package com.melalex.realworld
package commons.auth.service.impl

import commons.auth.model.{SecurityToken, UserPrincipalWithToken}
import commons.errors.model.RealWorldError
import test.fixture.{PropertiesFixture, UserFixture}
import test.spec.UnitTestSpec
import test.util.TimeSupport

import pdi.jwt.JwtCirce

class JwtTokenServiceSuite extends UnitTestSpec with PropertiesFixture with UserFixture with TimeSupport {

  private val jwtCirce        = new JwtCirce(clock)
  private val jwtTokenService = new JwtTokenService(realWorldProperties, jwtCirce)

  "verifyToken" should "return UserPrincipalWithToken for valid token" in {
    val userWithToken = jwtTokenService.generateNewTokenForUser(savedUser)

    jwtTokenService.validateToken(userWithToken.token) shouldEqual Right(UserPrincipalWithToken(actualUserPrincipal, userWithToken.token))
  }

  it should "return CredentialsException for invalid token" in {
    val errors = jwtTokenService.validateToken(SecurityToken("invalid token")).left.map(_.errors)

    errors shouldEqual Left(Seq(RealWorldError.InvalidToken))
  }

  "generateNewToken" should "return ValidSecurityToken" in {
    jwtTokenService.generateNewToken(actualUserPrincipal) shouldEqual UserFixture.ValidSecurityToken
  }

  it should "return ValidSecurityTokenAfterUpdate" in {
    jwtTokenService.generateNewToken(actualUserPrincipalAfterUpdate) shouldEqual UserFixture.ValidSecurityTokenAfterUpdate
  }
}
