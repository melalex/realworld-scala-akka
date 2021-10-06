package com.melalex.realworld
package users.service

import commons.model.Email
import users.model._

import java.time.{Clock, Instant}

class UserFactory(
    clock: Clock,
    passwordHashService: PasswordHashService
) {

  def createNewUser(newUser: NewUser): UnsavedUser = {
    val now = Instant.now(clock)

    UnsavedUser(
      email = Email(newUser.email),
      username = Username(newUser.username),
      password = passwordHashService.hash(newUser.password),
      createdAt = now,
      updatedAt = now
    )
  }

  def createUpdatedUser(user: SavedUser, updateUser: UpdateUser): SavedUser = user.copy(
    email = Email(updateUser.email),
    bio = updateUser.bio,
    image = updateUser.image,
    updatedAt = Instant.now(clock)
  )
}
