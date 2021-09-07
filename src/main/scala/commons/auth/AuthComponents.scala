package com.melalex.realworld
package commons.auth

import commons.auth.service.TokenService
import commons.auth.service.impl.JwtTokenService
import commons.util.InstantProvider
import config.RealWorldProperties

import com.softwaremill.macwire.wire

trait AuthComponents {

  def instantProvider: InstantProvider
  def realWorldProperties: RealWorldProperties

  val tokenService: TokenService = wire[JwtTokenService]
}
