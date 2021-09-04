package com.melalex.realworld
package commons.auth.web

import commons.auth.model.{SecurityToken, UserPrincipalWithToken}
import commons.auth.service.TokenService

import akka.http.scaladsl.server.{Directive1, Directives}

import scala.util.{Failure, Success}

trait RealWorldSecurityDirectives { self: Directives =>

  def authenticated(tokenService: TokenService): Directive1[UserPrincipalWithToken] =
    headerValueByName("Authorization")
      .map(_.split(" ").last)
      .map(SecurityToken)
      .map(tokenService.validateToken)
      .flatMap {
        case Success(value)     => provide(value)
        case Failure(exception) => failWith(exception)
      }
}

object RealWorldSecurityDirectives extends RealWorldSecurityDirectives with Directives
