package com.melalex.realworld
package commons.auth.model

import commons.model.{Email, ModelId}
import users.model.Username

sealed trait UserPrincipal {

  val id: ModelId
  val email: Email
  val username: Username
  val authenticated: Boolean
}

case class ActualUserPrincipal(
    id: ModelId,
    email: Email,
    username: Username
) extends UserPrincipal {

  override val authenticated: Boolean = true
}

object AnonymousUserPrincipal extends UserPrincipal {

  override val id: ModelId            = ModelId.Unsaved
  override val email: Email           = Email("anon@example.com")
  override val username: Username     = Username("Anon")
  override val authenticated: Boolean = false
}
