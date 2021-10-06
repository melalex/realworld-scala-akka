package com.melalex.realworld
package commons.auth.service.impl

import commons.auth.model.{ActualUserPrincipal, SecurityToken}
import commons.auth.service.TokenService
import commons.errors.model.{SecurityException, RealWorldError}
import commons.util.JavaConversions
import config.RealWorldProperties

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import java.time.Instant

class JwtTokenService(realWorldProperties: RealWorldProperties, jwtCirce: JwtCirce) extends TokenService with JavaConversions {

  override def validateToken(token: SecurityToken): Either[SecurityException, ActualUserPrincipal] =
    jwtCirce
      .decode(token.value, realWorldProperties.session.jwtSecretKey, Seq(JwtTokenService.EncodingAlgorithm))
      .toEither
      .flatMap(it => decode[ActualUserPrincipal](it.content))
      .left
      .map(ex => SecurityException(Seq(RealWorldError.InvalidToken), ex))

  override def generateNewToken(principal: ActualUserPrincipal): SecurityToken = {
    val now = Instant.now(jwtCirce.clock)
    val claim = JwtClaim(
      content = principal.asJson.noSpaces,
      issuer = Some(realWorldProperties.session.jwtIssuer),
      expiration = Some(now.plus(realWorldProperties.session.ttl.asJava).getEpochSecond),
      issuedAt = Some(now.getEpochSecond)
    )

    SecurityToken(JwtCirce.encode(claim, realWorldProperties.session.jwtSecretKey, JwtTokenService.EncodingAlgorithm))
  }
}

object JwtTokenService {

  val EncodingAlgorithm: JwtHmacAlgorithm = JwtAlgorithm.HS256
}
