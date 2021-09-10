package com.melalex.realworld
package test.fixture

import commons.auth.model.{ActualUserPrincipal, SecurityToken, UserPrincipalWithToken}
import commons.model.ModelId
import test.fixture.UserFixture._
import test.util.TimeSupport
import users.dto.{UserAuthenticationDto, UserDto, UserRegistrationDto, UserUpdateDto}
import users.model._

import org.scalactic.Uniformity

trait UserFixture {

  val userAuthenticationDto: UserAuthenticationDto = UserAuthenticationDto(
    UserAuthenticationDto.Body(
      email = Email,
      password = PlainTextPassword
    )
  )

  val userRegistrationDto: UserRegistrationDto = UserRegistrationDto(
    UserRegistrationDto.Body(
      username = Username,
      email = Email,
      password = PlainTextPassword
    )
  )

  val userUpdateDto: UserUpdateDto = UserUpdateDto(
    UserUpdateDto.Body(
      email = UpdatedEmail,
      bio = UpdatedBio,
      image = UpdatedImage
    )
  )

  val savedUserDto: UserDto = UserDto(
    UserDto.Body(
      email = Email,
      token = ValidSecurityToken.value,
      username = Username,
      bio = Bio,
      image = Image
    )
  )

  val savedUserAfterUpdateDto: UserDto = UserDto(
    UserDto.Body(
      email = UpdatedEmail,
      token = ValidSecurityTokenAfterUpdate.value,
      username = Username,
      bio = UpdatedBio,
      image = UpdatedImage
    )
  )

  val newUser: NewUser = NewUser(
    username = Username,
    email = Email,
    password = PlainTextPassword
  )

  val updateUser: UpdateUser = UpdateUser(
    email = UpdatedEmail,
    bio = UpdatedBio,
    image = UpdatedImage
  )

  val unsavedUser: UnsavedUser = UnsavedUser(
    email = Email,
    username = Username,
    password = Password,
    createdAt = TimeSupport.Now,
    updatedAt = TimeSupport.Now
  )

  val savedUser: SavedUser = SavedUser(
    id = Id,
    email = Email,
    username = Username,
    password = Password,
    bio = Bio,
    image = Image,
    createdAt = TimeSupport.Now,
    updatedAt = TimeSupport.Now
  )

  val savedUserAfterUpdate: SavedUser = SavedUser(
    id = Id,
    email = UpdatedEmail,
    username = Username,
    password = Password,
    bio = UpdatedBio,
    image = UpdatedImage,
    createdAt = TimeSupport.Now,
    updatedAt = TimeSupport.Now
  )

  val userWithTokenAfterUpdate: UserWithToken = UserWithToken(
    user = savedUserAfterUpdate,
    token = ValidSecurityTokenAfterUpdate
  )

  val userWithToken: UserWithToken = UserWithToken(
    user = savedUser,
    token = ValidSecurityToken
  )

  val actualUserPrincipal: ActualUserPrincipal = ActualUserPrincipal(
    id = Id,
    email = Email,
    username = Username
  )

  val actualUserPrincipalAfterUpdate: ActualUserPrincipal = ActualUserPrincipal(
    id = Id,
    email = UpdatedEmail,
    username = Username
  )

  val userPrincipalWithToken: UserPrincipalWithToken = UserPrincipalWithToken(
    principal = actualUserPrincipal,
    token = ValidSecurityToken
  )

  val unTokenized: Uniformity[UserDto] = new Uniformity[UserDto] {

    override def normalizedOrSame(b: Any): Any = b match {
      case s: UserDto => normalized(s)
      case _          => b
    }

    override def normalizedCanHandle(b: Any): Boolean = b.isInstanceOf[UserDto]

    override def normalized(a: UserDto): UserDto = a.copy(a.user.copy(token = TokenPlaceholder))
  }
}

object UserFixture {

  val Id: ModelId               = ModelId(1)
  val Email: String             = "test@test.com"
  val Username: String          = "Username"
  val PlainTextPassword: String = "Password"
  val Password: PasswordHash    = PasswordHash("$2a$10$Ff1/qYQliPCtCiIsidqX6eu.JFb3ab9moRtKhCuh2GTj9r2G81LKK")
  val Bio: Option[String]       = None
  val Image: Option[String]     = None
  val ValidSecurityToken: SecurityToken = SecurityToken(
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJyZWFsd29ybGQiLCJleHAiOjE2MzExNDU2MDAsImlhdCI6MTYzMTEwMjQwMCwiaWQiOnsidmFsdWUiOjF9LCJlbWFpbCI6InRlc3RAdGVzdC5jb20iLCJ1c2VybmFtZSI6IlVzZXJuYW1lIn0.rKu2XvmEhhKRyMldr496hh0UvrqdXhoCXeSd9zMRZy4")

  val UpdatedEmail: String       = "newtest@test.com"
  val UpdatedBio: Some[String]   = Some("NewBio")
  val UpdatedImage: Some[String] = Some("NewImage")
  val ValidSecurityTokenAfterUpdate: SecurityToken = SecurityToken(
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJyZWFsd29ybGQiLCJleHAiOjE2MzExNDU2MDAsImlhdCI6MTYzMTEwMjQwMCwiaWQiOnsidmFsdWUiOjF9LCJlbWFpbCI6Im5ld3Rlc3RAdGVzdC5jb20iLCJ1c2VybmFtZSI6IlVzZXJuYW1lIn0.nJ6fsstS4QlhLrdanECKuDXNNZMfAaVaCj_7wkWZSiU")

  private val TokenPlaceholder = "***"
}
