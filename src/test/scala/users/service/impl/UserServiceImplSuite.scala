package com.melalex.realworld
package users.service.impl

import commons.auth.service.TokenService
import commons.errors.model.{NotFoundException, RealWorldError, SecurityException}
import test.fixture.UserFixture
import test.spec.UnitTestSpec
import test.util.IdDbInterpreter
import users.repository.UserRepository
import users.service.{PasswordHashService, UserFactory}

import cats._
import org.mockito.scalatest.IdiomaticMockito

class UserServiceImplSuite extends UnitTestSpec with IdiomaticMockito with UserFixture {

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
    userRepository.findByEmail(UserFixture.Email).returns(Some(savedUser))
    passwordHash.verify(UserFixture.PlainTextPassword, UserFixture.Password).returns(true)
    tokenService.generateNewToken(actualUserPrincipal).returns(UserFixture.ValidSecurityToken)

    val actual = userService.authenticateUser(UserFixture.Email, UserFixture.PlainTextPassword)

    actual shouldEqual Right(userWithToken)
  }

  it should "return CredentialsException when credentials is invalid" in {
    userRepository.findByEmail(UserFixture.Email).returns(Some(savedUser))
    passwordHash.verify(UserFixture.PlainTextPassword, UserFixture.Password).returns(false)

    val actual = userService.authenticateUser(UserFixture.Email, UserFixture.PlainTextPassword)

    actual shouldEqual Left(SecurityException(Seq(RealWorldError.InvalidCredentials)))
  }

  it should "return CredentialsException when user not found" in {
    userRepository.findByEmail(UserFixture.Email).returns(None)

    val actual = userService.authenticateUser(UserFixture.Email, UserFixture.PlainTextPassword)

    actual shouldEqual Left(SecurityException(Seq(RealWorldError.InvalidCredentials)))
  }

  "createUser" should "return UserWithToken" in {
    userFactory.createNewUser(newUser).returns(unsavedUser)
    userRepository.save(unsavedUser).returns(savedUser)
    tokenService.generateNewToken(actualUserPrincipal).returns(UserFixture.ValidSecurityToken)

    val actual = userService.createUser(newUser)

    actual shouldEqual userWithToken
  }

  "updateUser" should "return UserWithToken" in {
    userRepository.findByUsername(UserFixture.Username).returns(Some(savedUser))
    userFactory.createUpdatedUser(savedUser, updateUser).returns(savedUserAfterUpdate)
    userRepository.save(savedUserAfterUpdate).returns(savedUserAfterUpdate)
    tokenService.generateNewToken(actualUserPrincipalAfterUpdate).returns(UserFixture.ValidSecurityTokenAfterUpdate)

    val actual = userService.updateUser(UserFixture.Username, updateUser)

    actual shouldEqual Right(userWithTokenAfterUpdate)
  }

  it should "return NotFoundException when no user found" in {
    userRepository.findByUsername(UserFixture.Username).returns(None)

    val actual = userService.updateUser(UserFixture.Username, updateUser)

    actual shouldEqual Left(NotFoundException(Seq(RealWorldError.NotFound(UserFixture.Username.value))))
  }

  "getUserById" should "return UserWithToken" in {
    userRepository.findByUsername(UserFixture.Username).returns(Some(savedUser))
    tokenService.generateNewToken(actualUserPrincipal).returns(UserFixture.ValidSecurityToken)

    val actual = userService.getUserByUsername(UserFixture.Username)

    actual shouldEqual Right(userWithToken)
  }

  it should "return NotFoundException when no user found" in {
    userRepository.findByUsername(UserFixture.Username).returns(None)

    val actual = userService.getUserByUsername(UserFixture.Username)

    actual shouldEqual Left(NotFoundException(Seq(RealWorldError.NotFound(UserFixture.Username.value))))
  }
}
