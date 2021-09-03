package com.melalex.realworld
package users.service

import commons.util.InstantProvider
import users.model._

class UserFactory(
    instantProvider: InstantProvider,
    passwordHashService: PasswordHashService
) {

  def createNewUser(newUser: NewUser): UnSavedUser = {
    val now = instantProvider.provide()

    UnSavedUser(
      email = newUser.email,
      username = newUser.username,
      password = passwordHashService.hash(newUser.password),
      bio = None,
      image = None,
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
