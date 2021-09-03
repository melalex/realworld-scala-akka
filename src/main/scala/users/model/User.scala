package com.melalex.realworld
package users.model

import commons.auth.model.{ActualUserPrincipal, SecurityToken}
import commons.model.ModelId

import java.time.Instant

sealed trait User {

  val email: String
  val username: String
  val password: PasswordHash
  val bio: Option[String]
  val image: Option[String]
  val createdAt: Instant
  val updatedAt: Instant
}

case class SavedUser(
    id: ModelId,
    email: String,
    username: String,
    password: PasswordHash,
    bio: Option[String],
    image: Option[String],
    createdAt: Instant,
    updatedAt: Instant
) extends User

case class UnSavedUser(
    email: String,
    username: String,
    password: PasswordHash,
    bio: Option[String],
    image: Option[String],
    createdAt: Instant,
    updatedAt: Instant
) extends User

object User {

  implicit class SavedUserOps(val value: SavedUser) extends AnyVal {

    def asPrincipal: ActualUserPrincipal = ActualUserPrincipal(
      id = value.id,
      email = value.email,
      username = value.username
    )

    def withToken(token: SecurityToken): UserWithToken = UserWithToken(value, token)
  }

  implicit class UnSavedUserOps(val value: UnSavedUser) extends AnyVal {

    def asSaved(id: ModelId): SavedUser = SavedUser(
      id = id,
      email = value.email,
      username = value.username,
      password = value.password,
      bio = value.bio,
      image = value.image,
      createdAt = value.createdAt,
      updatedAt = value.updatedAt
    )
  }
}
