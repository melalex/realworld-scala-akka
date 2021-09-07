package com.melalex.realworld
package commons.auth.service

import commons.auth.model.{ActualUserPrincipal, SecurityToken, UserPrincipalWithToken}
import commons.errors.model.CredentialsException
import users.model.{SavedUser, UserWithToken}

trait TokenService {

  def validateToken(token: SecurityToken): Either[CredentialsException, UserPrincipalWithToken]

  def generateNewToken(principal: ActualUserPrincipal): SecurityToken
}

object TokenService {

  implicit class TokenServiceOps(val value: TokenService) extends AnyVal {

    def generateNewTokenForUser(user: SavedUser): UserWithToken = user.withToken(value.generateNewToken(user.asPrincipal))
  }
}
