package com.melalex.realworld
package users.model

import commons.auth.model.{ActualUserPrincipal, SecurityToken}
import commons.model.{Email, FieldName, ModelId}

import java.time.Instant

sealed trait User {

  val email: Email
  val username: Username
  val password: PasswordHash
  val createdAt: Instant
  val updatedAt: Instant
}

case class SavedUser(
    id: ModelId,
    email: Email,
    username: Username,
    password: PasswordHash,
    bio: Option[String],
    image: Option[String],
    createdAt: Instant,
    updatedAt: Instant
) extends User

case class UnsavedUser(
    email: Email,
    username: Username,
    password: PasswordHash,
    createdAt: Instant,
    updatedAt: Instant
) extends User

object User {

  val Id: FieldName[User]        = FieldName[User]("id")
  val Email: FieldName[User]     = FieldName[User]("email")
  val Username: FieldName[User]  = FieldName[User]("username")
  val Password: FieldName[User]  = FieldName[User]("password")
  val Bio: FieldName[User]       = FieldName[User]("bio")
  val Image: FieldName[User]     = FieldName[User]("image")
  val CreatedAt: FieldName[User] = FieldName[User]("createdAt")
  val UpdatedAt: FieldName[User] = FieldName[User]("updatedAt")

  implicit class SavedUserOps(val value: SavedUser) extends AnyVal {

    def asPrincipal: ActualUserPrincipal = ActualUserPrincipal(
      id = value.id,
      email = value.email,
      username = value.username
    )

    def withToken(token: SecurityToken): UserWithToken = UserWithToken(value, token)
  }

  implicit class UnSavedUserOps(val value: UnsavedUser) extends AnyVal {

    def asSaved(id: ModelId): SavedUser = SavedUser(
      id = id,
      email = value.email,
      username = value.username,
      password = value.password,
      bio = None,
      image = None,
      createdAt = value.createdAt,
      updatedAt = value.updatedAt
    )
  }
}
