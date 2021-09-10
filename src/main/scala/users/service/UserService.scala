package com.melalex.realworld
package users.service

import commons.errors.model.{CredentialsException, NotFoundException}
import commons.model.ModelId
import users.model.{NewUser, UpdateUser, UserWithToken}

trait UserService[F[_]] {

  def authenticateUser(email: String, password: String): F[Either[CredentialsException, UserWithToken]]

  def createUser(newUser: NewUser): F[UserWithToken]

  def updateUser(id: ModelId, updateUser: UpdateUser): F[Either[NotFoundException, UserWithToken]]

  def getUserById(id: ModelId): F[Either[NotFoundException, UserWithToken]]
}
