package com.melalex.realworld
package users.service.impl

import commons.auth.service.TokenService
import commons.db.DbInterpreter
import commons.errors.model.{SecurityException, NotFoundException, RealWorldError}
import commons.model.Email
import users.model.{NewUser, UpdateUser, UserWithToken, Username}
import users.repository.UserRepository
import users.service.{PasswordHashService, UserFactory, UserService}

import cats._
import cats.data._
import cats.implicits._

import scala.concurrent.ExecutionContext

class UserServiceImpl[F[_]: Monad, DB[_]: Monad](
    userRepository: UserRepository[DB],
    dbInterpreter: DbInterpreter[F, DB],
    tokenService: TokenService,
    passwordHash: PasswordHashService,
    userFactory: UserFactory
)(implicit executionContext: ExecutionContext)
    extends UserService[F] {

  def authenticateUser(email: Email, password: String): F[Either[SecurityException, UserWithToken]] = {
    val unitOfWork = OptionT(userRepository.findByEmail(email))
      .filter(passwordHash.verifyForUser(password))
      .map(tokenService.generateNewTokenForUser)
      .toRight(RealWorldError.InvalidCredentials.ex[SecurityException])

    dbInterpreter.execute(unitOfWork.value)
  }

  def createUser(newUser: NewUser): F[UserWithToken] = {
    val unitOfWork = userRepository
      .save(userFactory.createNewUser(newUser))
      .map(tokenService.generateNewTokenForUser)

    dbInterpreter.executeTransitionally(unitOfWork)
  }

  def updateUser(username: Username, updateUser: UpdateUser): F[Either[NotFoundException, UserWithToken]] = {
    val unitOfWork = OptionT(userRepository.findByUsername(username))
      .map(user => userFactory.createUpdatedUser(user, updateUser))
      .semiflatMap(userRepository.save)
      .map(tokenService.generateNewTokenForUser)
      .toRight(RealWorldError.NotFound(username.value).ex[NotFoundException])

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  def getUserByUsername(username: Username): F[Either[NotFoundException, UserWithToken]] = {
    val unitOfWork = OptionT(userRepository.findByUsername(username))
      .map(tokenService.generateNewTokenForUser)
      .toRight(RealWorldError.NotFound(username.value).ex[NotFoundException])

    dbInterpreter.execute(unitOfWork.value)
  }
}
