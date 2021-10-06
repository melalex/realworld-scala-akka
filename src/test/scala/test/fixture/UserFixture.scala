package com.melalex.realworld
package test.fixture

import commons.auth.model.{ActualUserPrincipal, SecurityToken}
import commons.model.{ModelId, Email => TEmail}
import test.fixture.UserFixture._
import test.util.TimeSupport
import users.dto.{UserAuthenticationDto, UserDto, UserRegistrationDto, UserUpdateDto}
import users.model.{NewUser, PasswordHash, SavedUser, UnsavedUser, UpdateUser, UserWithToken, Username => TUsername}

import org.scalactic.Uniformity

trait UserFixture {

  val userAuthenticationDto: UserAuthenticationDto = UserAuthenticationDto(
    UserAuthenticationDto.Body(
      email = Email.value,
      password = PlainTextPassword
    )
  )

  val userRegistrationDto: UserRegistrationDto = UserRegistrationDto(
    UserRegistrationDto.Body(
      username = Username.value,
      email = Email.value,
      password = PlainTextPassword
    )
  )

  val userUpdateDto: UserUpdateDto = UserUpdateDto(
    UserUpdateDto.Body(
      email = UpdatedEmail.value,
      bio = UpdatedBio,
      image = UpdatedImage
    )
  )

  val savedUserDto: UserDto = UserDto(
    UserDto.Body(
      email = Email.value,
      token = ValidSecurityToken.value,
      username = Username.value,
      bio = Bio,
      image = Image
    )
  )

  val savedUserAfterUpdateDto: UserDto = UserDto(
    UserDto.Body(
      email = UpdatedEmail.value,
      token = ValidSecurityTokenAfterUpdate.value,
      username = Username.value,
      bio = UpdatedBio,
      image = UpdatedImage
    )
  )

  val newUser: NewUser = NewUser(
    username = Username.value,
    email = Email.value,
    password = PlainTextPassword
  )

  val updateUser: UpdateUser = UpdateUser(
    email = UpdatedEmail.value,
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
  val Email: TEmail             = TEmail("test@test.com")
  val Username: TUsername       = TUsername("Username")
  val PlainTextPassword: String = "Password"
  val Password: PasswordHash    = PasswordHash("$2a$10$Ff1/qYQliPCtCiIsidqX6eu.JFb3ab9moRtKhCuh2GTj9r2G81LKK")
  val Bio: Option[String]       = None
  val Image: Option[String]     = None
  val ValidSecurityToken: SecurityToken = SecurityToken(
   "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJyZWFsd29ybGQiLCJleHAiOjE2MzExNDU2MDAsImlhdCI6MTYzMTEwMjQwMCwiaWQiOnsidmFsdWUiOjF9LCJlbWFpbCI6eyJ2YWx1ZSI6InRlc3RAdGVzdC5jb20ifSwidXNlcm5hbWUiOnsidmFsdWUiOiJVc2VybmFtZSJ9fQ.2V2i_jIhMY_UsPGn_9uKmnLtsKJSr26T5RRHe9pfaRE"
  )

  val UpdatedEmail: TEmail       = TEmail("newtest@test.com")
  val UpdatedBio: Some[String]   = Some("NewBio")
  val UpdatedImage: Some[String] = Some("NewImage")
  val ValidSecurityTokenAfterUpdate: SecurityToken = SecurityToken(
   "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJyZWFsd29ybGQiLCJleHAiOjE2MzExNDU2MDAsImlhdCI6MTYzMTEwMjQwMCwiaWQiOnsidmFsdWUiOjF9LCJlbWFpbCI6eyJ2YWx1ZSI6Im5ld3Rlc3RAdGVzdC5jb20ifSwidXNlcm5hbWUiOnsidmFsdWUiOiJVc2VybmFtZSJ9fQ.lVbBlxrW5qoLavk-olP6fyjvz8i3aUEPs_pqcQ5d_5Y"
  )

  private val TokenPlaceholder = "***"
}
