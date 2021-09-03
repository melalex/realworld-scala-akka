package com.melalex.realworld
package commons.auth.service

import commons.auth.model.{ActualUserPrincipal, SecurityToken, UserPrincipalWithToken}
import users.model.{SavedUser, UserWithToken}

import scala.util.Try

trait TokenService {

  def validateToken(token: SecurityToken): Try[UserPrincipalWithToken]

  def generateNewToken(principal: ActualUserPrincipal): SecurityToken
}

object TokenService {

  implicit class TokenServiceOps(val value: TokenService) extends AnyVal {

    def generateNewTokenForUser(user: SavedUser): UserWithToken = user.withToken(value.generateNewToken(user.asPrincipal))
  }
}
