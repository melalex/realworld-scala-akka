package com.melalex.realworld
package users.service.impl

import commons.auth.service.TokenService
import commons.db.DbInterpreter
import commons.errors.model.RealWorldError
import commons.errors.{NotFoundException, SecurityException}
import commons.model.ModelId
import users.model.{NewUser, SavedUser, UpdateUser, UserWithToken}
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

  def authenticateUser(email: String, password: String): F[Either[SecurityException, UserWithToken]] = {
    val unitOfWork = OptionT(userRepository.findByEmail(email))
      .filter(passwordHash.verify(password))
      .map(tokenService.generateNewTokenForUser)
      .toRight(RealWorldError.InvalidCredentials.ex[SecurityException])

    dbInterpreter.execute(unitOfWork.value)
  }

  def createUser(newUser: NewUser): F[UserWithToken] = {
    val unitOfWork = userRepository
      .save(userFactory.createNewUser(newUser))
      .map(tokenService.generateNewTokenForUser)

    dbInterpreter.execute(unitOfWork)
  }

  def updateUser(id: ModelId, updateUser: UpdateUser): F[Either[NotFoundException, SavedUser]] = {
    val unitOfWork = OptionT(userRepository.findById(id))
      .map(user => userFactory.createUpdatedUser(user, updateUser))
      .map(userRepository.save)
      .toRight(RealWorldError.NotFound(id).ex[NotFoundException])

    dbInterpreter.executeTransitionally(unitOfWork.value)
  }

  def getUserById(id: ModelId): F[Either[NotFoundException, SavedUser]] = {
    val unitOfWork = OptionT(userRepository.findById(id))
      .toRight(RealWorldError.NotFound(id).ex[NotFoundException])

    dbInterpreter.execute(unitOfWork.value)
  }
}
