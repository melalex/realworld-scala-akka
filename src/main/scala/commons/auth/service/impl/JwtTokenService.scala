package com.melalex.realworld
package commons.auth.service.impl

import commons.auth.model.{ActualUserPrincipal, SecurityToken, UserPrincipalWithToken}
import commons.auth.service.TokenService

import scala.util.Try

class JwtTokenService extends TokenService {

  override def validateToken(token: SecurityToken): Try[UserPrincipalWithToken] = ???

  override def generateNewToken(principal: ActualUserPrincipal): SecurityToken = ???
}
