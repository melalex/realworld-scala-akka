package com.melalex.realworld
package users.dto

import commons.validation.RealWorldValidation
import users.model.User

import cats.implicits._

case class UserRegistrationDto(
    user: UserRegistrationDto.Body
)

object UserRegistrationDto extends RealWorldValidation {

  case class Body(
      username: String,
      email: String,
      password: String
  )

  private val UsernameValidator = validator[String](User.Username.messageKey, alphanumeric, min(4), max(40)) _
  private val EmailValidator    = validator[String](User.Email.messageKey, required, email) _
  private val PasswordValidator = validator[String](User.Password.messageKey, min(6), max(40)) _

  implicit lazy val formValidation: FormValidation[UserRegistrationDto] = {
    case UserRegistrationDto(Body(username, userEmail, password)) =>
      (UsernameValidator(username), EmailValidator(userEmail), PasswordValidator(password))
        .mapN(Body)
        .map(UserRegistrationDto(_))
  }
}
