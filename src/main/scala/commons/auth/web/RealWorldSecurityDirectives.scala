package com.melalex.realworld
package commons.auth.web

import commons.auth.model.{SecurityToken, UserPrincipalWithToken}
import commons.auth.service.TokenService

import akka.http.scaladsl.server.{Directive1, Directives}

trait RealWorldSecurityDirectives { self: Directives =>

  def authenticated(tokenService: TokenService): Directive1[UserPrincipalWithToken] =
    headerValueByName("Authorization")
      .map(_.split(" ").last)
      .map(SecurityToken)
      .map(tokenService.validateToken)
      .flatMap {
        case Right(value)    => provide(value)
        case Left(exception) => failWith(exception)
      }
}

object RealWorldSecurityDirectives extends RealWorldSecurityDirectives with Directives
