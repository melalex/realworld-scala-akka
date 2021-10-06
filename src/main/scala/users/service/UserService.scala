package com.melalex.realworld
package users.service

import commons.errors.model.{SecurityException, NotFoundException}
import commons.model.Email
import users.model.{NewUser, UpdateUser, UserWithToken, Username}

trait UserService[F[_]] {

  def authenticateUser(email: Email, password: String): F[Either[SecurityException, UserWithToken]]

  def createUser(newUser: NewUser): F[UserWithToken]

  def updateUser(username: Username, updateUser: UpdateUser): F[Either[NotFoundException, UserWithToken]]

  def getUserByUsername(username: Username): F[Either[NotFoundException, UserWithToken]]
}
