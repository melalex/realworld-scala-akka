package com.melalex.realworld
package users.service

import commons.errors.model.{NotFoundException, CredentialsException}
import commons.model.ModelId
import users.model.{NewUser, SavedUser, UpdateUser, UserWithToken}

trait UserService[F[_]] {

  def authenticateUser(email: String, password: String): F[Either[CredentialsException, UserWithToken]]

  def createUser(newUser: NewUser): F[UserWithToken]

  def updateUser(id: ModelId, updateUser: UpdateUser): F[Either[NotFoundException, SavedUser]]

  def getUserById(id: ModelId): F[Either[NotFoundException, SavedUser]]
}
