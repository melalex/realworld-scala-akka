package com.melalex.realworld
package commons.auth.web

import commons.auth.model.{SecurityToken, UserPrincipalWithToken}
import commons.auth.service.TokenService

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.server.{Directive1, Directives}

trait RealWorldSecurityDirectives { self: Directives =>

  def authenticated(tokenService: TokenService): Directive1[UserPrincipalWithToken] =
    headerValue(extractSecurityToken)
      .map(tokenService.validateToken)
      .flatMap {
        case Right(value)    => provide(value)
        case Left(exception) => failWith(exception)
      }

  private def extractSecurityToken: HttpHeader => Option[SecurityToken] = {
    case header: Authorization => Some(SecurityToken(header.credentials.token()))
    case _                     => None
  }
}

object RealWorldSecurityDirectives extends RealWorldSecurityDirectives with Directives
