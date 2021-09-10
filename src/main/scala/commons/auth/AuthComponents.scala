package com.melalex.realworld
package commons.auth

import commons.auth.service.TokenService
import commons.auth.service.impl.JwtTokenService
import config.RealWorldProperties

import com.softwaremill.macwire.wire
import pdi.jwt.JwtCirce

import java.time.Clock

trait AuthComponents {

  def realWorldProperties: RealWorldProperties
  def clock: Clock

  lazy val tokenService: TokenService = wire[JwtTokenService]

  private[auth] lazy val jwtCirce: JwtCirce = wire[JwtCirce]
}
