package com.melalex.realworld
package commons.auth.web

import commons.auth.model.{ActualUserPrincipal, AnonymousUserPrincipal, SecurityToken, UserPrincipal}
import commons.auth.service.TokenService
import commons.errors.model.{CredentialsException, RealWorldError}

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._

trait TokenAuthorizationDirectives {

  def authenticated(tokenService: TokenService): Directive1[ActualUserPrincipal] =
    optionalHeaderValue(extractSecurityToken).map {
      case Some(value) => tokenService.validateToken(value)
      case None        => Left(RealWorldError.InvalidToken.ex[CredentialsException])
    }.flatMap(downStream)

  def maybeAuthenticated(tokenService: TokenService): Directive1[UserPrincipal] =
    optionalHeaderValue(extractSecurityToken).map {
      case Some(value) => tokenService.validateToken(value)
      case None        => Right(AnonymousUserPrincipal)
    }.flatMap(downStream)

  private def extractSecurityToken: HttpHeader => Option[SecurityToken] = {
    case header: Authorization => Some(SecurityToken(header.credentials.token()))
    case _                     => None
  }

  private def downStream[A <: UserPrincipal]: PartialFunction[Either[CredentialsException, A], Directive1[A]] = {
    case Right(value)    => provide(value)
    case Left(exception) => failWith(exception)
  }
}

object TokenAuthorizationDirectives extends TokenAuthorizationDirectives
