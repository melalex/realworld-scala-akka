package com.melalex.realworld
package commons.auth.service.impl

import commons.auth.model.{ActualUserPrincipal, SecurityToken, UserPrincipalWithToken}
import commons.auth.service.TokenService
import commons.errors.model.{CredentialsException, RealWorldError}
import commons.util.InstantProvider
import config.RealWorldProperties

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

class JwtTokenService(instantProvider: InstantProvider, realWorldProperties: RealWorldProperties) extends TokenService {

  override def validateToken(token: SecurityToken): Either[CredentialsException, UserPrincipalWithToken] =
    JwtCirce
      .decode(token.value, realWorldProperties.session.jwtSecretKey, Seq(JwtTokenService.EncodingAlgorithm))
      .toEither
      .flatMap(it => decode[ActualUserPrincipal](it.content))
      .map(UserPrincipalWithToken(_, token))
      .left
      .map(ex => CredentialsException(Seq(RealWorldError.InvalidToken), ex))

  override def generateNewToken(principal: ActualUserPrincipal): SecurityToken = {
    val now = instantProvider.provide()
    val claim = JwtClaim(
      content = principal.asJson.noSpaces,
      issuer = Some(realWorldProperties.session.jwtIssuer),
      expiration = Some(now.plus(realWorldProperties.session.ttl).getEpochSecond),
      issuedAt = Some(now.getEpochSecond)
    )

    SecurityToken(JwtCirce.encode(claim, realWorldProperties.session.jwtSecretKey, JwtTokenService.EncodingAlgorithm))
  }
}

object JwtTokenService {

  val EncodingAlgorithm: JwtHmacAlgorithm = JwtAlgorithm.HS256
}
