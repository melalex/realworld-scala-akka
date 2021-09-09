package com.melalex.realworld
package users.dto

import commons.validation.RealWorldValidation
import users.model.User

import cats.implicits._

case class UserUpdateDto(
    user: UserUpdateDto.Body
)

object UserUpdateDto extends RealWorldValidation {

  case class Body(
      email: String,
      bio: Option[String],
      image: Option[String]
  )

  private val EmailValidator = validator[String](User.Email.messageKey, required, email) _
  private val BioValidator   = validator[Option[String]](User.Bio.messageKey, max(250)) _
  private val ImageValidator = validator[Option[String]](User.Image.messageKey, max(400)) _

  implicit lazy val formValidation: FormValidation[UserUpdateDto] = {
    case UserUpdateDto(Body(email, bio, image)) =>
      (EmailValidator(email), BioValidator(bio), ImageValidator(image))
        .mapN(Body)
        .map(UserUpdateDto(_))
  }
}
