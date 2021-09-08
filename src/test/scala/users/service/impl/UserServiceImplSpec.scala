package com.melalex.realworld
package users.service.impl

import commons.auth.service.TokenService
import commons.errors.model.{CredentialsException, NotFoundException, RealWorldError}
import fixture.{IdDbInterpreter, RealWorldSpec, UserFixture}
import users.repository.UserRepository
import users.service.{PasswordHashService, UserFactory}

import cats._
import org.mockito.scalatest.IdiomaticMockito

class UserServiceImplSpec extends RealWorldSpec with IdiomaticMockito with UserFixture {

  private lazy val userRepository = mock[UserRepository[Id]]
  private lazy val dbInterpreter  = IdDbInterpreter
  private lazy val tokenService   = mock[TokenService]
  private lazy val passwordHash   = mock[PasswordHashService]
  private lazy val userFactory    = mock[UserFactory]

  private lazy val userService = new UserServiceImpl(
    userRepository,
    dbInterpreter,
    tokenService,
    passwordHash,
    userFactory
  )

  "authenticateUser" should "return UserWithToken when credentials is valid" in {
    userRepository.findByEmail(UserFixture.Email) returns Some(savedUser)
    passwordHash.verify(UserFixture.PlainTextPassword, UserFixture.Password) returns true
    tokenService.generateNewToken(actualUserPrincipal) returns UserFixture.ValidSecurityToken

    val actual = userService.authenticateUser(UserFixture.Email, UserFixture.PlainTextPassword)

    actual shouldBe Right(userWithToken)
  }

  it should "return CredentialsException when credentials is invalid" in {
    userRepository.findByEmail(UserFixture.Email) returns Some(savedUser)
    passwordHash.verify(UserFixture.PlainTextPassword, UserFixture.Password) returns false

    val actual = userService.authenticateUser(UserFixture.Email, UserFixture.PlainTextPassword)

    actual shouldBe Left(CredentialsException(Seq(RealWorldError.InvalidCredentials)))
  }

  it should "return CredentialsException when user not found" in {
    userRepository.findByEmail(UserFixture.Email) returns None

    val actual = userService.authenticateUser(UserFixture.Email, UserFixture.PlainTextPassword)

    actual shouldBe Left(CredentialsException(Seq(RealWorldError.InvalidCredentials)))
  }

  "createUser" should "return UserWithToken" in {
    userFactory.createNewUser(newUser) returns unSavedUser
    userRepository.save(unSavedUser) returns savedUser
    tokenService.generateNewToken(actualUserPrincipal) returns UserFixture.ValidSecurityToken

    val actual = userService.createUser(newUser)

    actual shouldBe userWithToken
  }

  "updateUser" should "return SavedUser" in {
    userRepository.findById(UserFixture.Id) returns Some(savedUser)
    userFactory.createUpdatedUser(savedUser, updateUser) returns savedUserAfterUpdate
    userRepository.save(savedUserAfterUpdate) returns savedUserAfterUpdate

    val actual = userService.updateUser(UserFixture.Id, updateUser)

    actual shouldBe Right(savedUserAfterUpdate)
  }

  it should "return NotFoundException when no user found" in {
    userRepository.findById(UserFixture.Id) returns None

    val actual = userService.updateUser(UserFixture.Id, updateUser)

    actual shouldBe Left(NotFoundException(Seq(RealWorldError.NotFound(UserFixture.Id))))
  }

  "getUserById" should "return SavedUser" in {
    userRepository.findById(UserFixture.Id) returns Some(savedUser)

    val actual = userService.getUserById(UserFixture.Id)

    actual shouldBe Right(savedUser)
  }

  it should "return NotFoundException when no user found" in {
    userRepository.findById(UserFixture.Id) returns None

    val actual = userService.getUserById(UserFixture.Id)

    actual shouldBe Left(NotFoundException(Seq(RealWorldError.NotFound(UserFixture.Id))))
  }
}
