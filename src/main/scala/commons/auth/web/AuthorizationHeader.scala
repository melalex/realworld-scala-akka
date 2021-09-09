package com.melalex.realworld
package commons.auth.web

import commons.auth.model.SecurityToken

import akka.http.scaladsl.model.headers.{ModeledCustomHeader, ModeledCustomHeaderCompanion}

import scala.util.Try

final class AuthorizationHeader(val token: SecurityToken) extends ModeledCustomHeader[AuthorizationHeader] {

  override def companion: ModeledCustomHeaderCompanion[AuthorizationHeader] = AuthorizationHeader

  override def value(): String = s"Token ${token.value}"

  override def renderInRequests(): Boolean = true

  override def renderInResponses(): Boolean = false
}

object AuthorizationHeader extends ModeledCustomHeaderCompanion[AuthorizationHeader] {

  override def name: String = "Authorization"

  override def parse(value: String): Try[AuthorizationHeader] = Try(AuthorizationHeader(SecurityToken(value.split(" ").last)))

  def apply(token: SecurityToken): AuthorizationHeader = new AuthorizationHeader(token)
}
