package com.melalex.realworld
package commons.auth.service

import commons.auth.model.{ActualUserPrincipal, SecurityToken}
import commons.errors.model.SecurityException
import users.model.{SavedUser, UserWithToken}

trait TokenService {

  def validateToken(token: SecurityToken): Either[SecurityException, ActualUserPrincipal]

  def generateNewToken(principal: ActualUserPrincipal): SecurityToken
}

object TokenService {

  implicit class TokenServiceOps(val value: TokenService) extends AnyVal {

    def generateNewTokenForUser(user: SavedUser): UserWithToken = user.withToken(value.generateNewToken(user.asPrincipal))
  }
}
