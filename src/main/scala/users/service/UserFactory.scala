package com.melalex.realworld
package users.service

import commons.util.InstantProvider
import users.model._

class UserFactory(
    instantProvider: InstantProvider,
    passwordHashService: PasswordHashService
) {

  def createNewUser(newUser: NewUser): UnsavedUser = {
    val now = instantProvider.provide()

    UnsavedUser(
      email = newUser.email,
      username = newUser.username,
      password = passwordHashService.hash(newUser.password),
      createdAt = now,
      updatedAt = now
    )
  }

  def createUpdatedUser(user: SavedUser, updateUser: UpdateUser): SavedUser = user.copy(
    email = updateUser.email,
    bio = updateUser.bio,
    image = updateUser.image,
    updatedAt = instantProvider.provide()
  )
}
